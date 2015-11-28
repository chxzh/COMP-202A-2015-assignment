import clipboard as cb
import time, sys, re
import yaml
try:
    from yaml import CLoader as Loader
except ImportError:
    from yaml import Loader as Loader

class _Score_point():
    def __init__(self, subject, action, total,
                 remark="", deduction=0, deducted=False):
        self.subject = subject
        self.action = action
        self.total = total
        self.remark = remark
        self.deduction = deduction
        self.deducted = deducted
        
    def reset(self):
        self.remark = ""
        self.deduction = 0
        self.deducted = False
        
    def deduct(self, deduction, remark=""):
        if deduction < 0:
            raise TypeError("deduction should not be negative: %d")
        self.deduction = deduction
        self.remark = remark
        if deduction > 0: self.deducted = True
    
    def ask(self):
        return "(%d pt) Did %s %s?" % \
                (self.total, self.subject, self.action)
    
    def answer(self):
        response = ""
        if self.deducted:
            response = "(-%d) %s didn't %s." % \
                    (self.deduction, self.subject, self.action)
        else:
            response = "%s did %s." % (self.subject, self.action)
        if self.remark != "":
            response += " %s" % (self.remark)
        return response
     
    def brief_answer(self):
        response = ''
        if self.deducted:
            response = '(-%d) no.' % self.deduction
        else:
            response = "yes."            
        if self.remark != "":
            response += " %s" % self.remark
        return response


class _Grumble():
    def __init__(self, deduction, remark):
        self.deduction = abs(deduction)
        self.remark = remark
        self.brief_answer = self.answer
    
    def ask(self):
        return 'grumbled that "(-%d) %s"?'% (self.deduction, self.remark)
    
    def answer(self):
        return "(-%d) %s." % (self.deduction, self.remark)


class _Subsection():
    def __init__(self, subject, subsec_dic):
        self.subject = subject
        self.total = subsec_dic.pop("total")
        self.fullmark = self.total
        try:
            self.score_points = [_Score_point(self.subject, action, total)\
                    for action, total in subsec_dic["deduction"].iteritems()]
        except KeyError:
            # no deduction specified, add an general one.
            self.score_points = [_Score_point(self.subject, "do well", self.total)]
            
    def reset(self):
        self.total = self.fullmark
        for score_point in self.score_points:
            score_point.reset()
    
    def report(self):
        report = ""
        for score_point in self.score_points:
            if score_point.deducted:
                self.total -= score_point.deduction
                report += "\n - %s" % (score_point.answer())
        self.total = max(0, self.total)
        report = "(%d/%d) %s:%s" % \
                (self.total, self.fullmark, self.subject, report)
        return report


class _Overall_Section(): # on the same level of _Subsection
    def __init__(self, sec_title, subsec_dic={"deduction":{}}):            
        self.subject = sec_title
        self.deduction = 0
        self.score_points = [_Score_point(sec_title, action, total)\
                for action, total in subsec_dic["deduction"].iteritems()]
        self.extra_remark = []  # [(deduction, remark)]
    
    def reset(self):
        self.deduction = 0
        self.extra_remark = []
        for score_point in self.score_points:
            score_point.reset()
    
    def report(self):            
        report = ""
        for score_point in self.score_points:
            if score_point.deducted:
                self.deduction += score_point.deduction
                report += "\n - %s" % (score_point.answer)
        self.deduction = max(0, self.deduction)
        if self.deduction != 0:
            report = "(-%d) overall:%s" % \
                    (self.deduction, report)
        else:
            report = ""
        return report
                
                
class _Section():
    def __init__(self, title, sec_dic):
        self.title = title
        self.total = sec_dic.pop("total")
        self.fullmark = self.total
        try:
            self.overall = _Overall_Section(self.title,
                                       sec_dic.pop("overall"))
        except KeyError:
            self.overall = _Overall_Section(self.title)
        self.subsections = [_Subsection(subject, subsec_dic)\
                            for subject, subsec_dic in sec_dic.iteritems()]
    
    def reset(self):
        self.total = self.fullmark
        for subsec in self.subsections:
            subsec.reset()
        self.overall.reset()
    
    def report(self):
        report = ""
        self.total = 0
        for subsec in self.subsections:
            report += "\n%s" % subsec.report()
            self.total += subsec.total
        if self.overall.deduction != 0:
            report += "\n%s" % self.overall.report()
            self.total -= self.overall.deduction
        self.total = max(self.total, 0)
        report = "---%s (%d/%d)---%s" % \
                (self.title, self.total, self.fullmark, report)
        return report


class Grader:
    def __init__(self, rubric_path):
        with open(rubric_path, 'r') as rubric_file:
            rubric_dic = yaml.load(rubric_file, Loader)
        try:
            # load compulsory components
            self.fullmark = rubric_dic.pop("fullmark")
            self.overall = rubric_dic.pop("overall")
        except KeyError as err:
            raise Exception("cannot have root component %s in rubric file %d" % 
                            (err.args[0], rubric_path))
        # load optional components
        self.signature = self._load_optional(rubric_dic, 'signature', '')        
        try:
            self.overall = _Overall_Section("author", rubric_dic.pop("_overall"))
        except KeyError:
            self.overall = _Overall_Section("author")
        self.sections = [_Section(title, sec_dic) \
                         for title, sec_dic in rubric_dic.iteritems()]
        self.remark = ""
        self.total = self.fullmark
        self.records = []
        self.grumble_list = []
        self._score_point_generator = self._generate_score_points()
        self._next = None
        self._cur = None
        self._rewind_one = None
        self._rewind_index = -1 # always a negative index
        self.has_next = False
        self._clamp = lambda toclamp, total: max(min(toclamp, total), -total)
        self._get_next_ready()
    
    def _get_next_ready(self):
        try:
            self._next = self._score_point_generator.next()
        except StopIteration:
            self._next = None
            self.has_next = False
        else:
            self.has_next = True

    def _generate_score_points(self):
        for section in self.sections:
            for subsection in section.subsections:
                for score_point in subsection.score_points:
                    yield score_point
            for score_point in section.overall.score_points:
                yield score_point
        for score_point in self.overall.score_points:
            yield score_point            
    
    def _load_optional(self, dic, key, backup):
        try:
            return dic.pop(key)
        except KeyError:
            return backup    
    
    def reset(self):
        self.remark = ""
        self.score = self.fullmark
        self.records = []
        self.grumble_list = []
        for section in self.sections:
            section.reset()
        self._score_point_generator = self._generate_score_points()
        self._next = None
        self._cur = None
        self._rewind_one = None
        self.has_next = False
        self._get_next_ready()
            
    def grade_next(self):
        self._cur = self._next
        self._get_next_ready()
        pass
    
    def save_current(self, deduction, remark, deducted=False):
        deduction = self._clamp(deduction, self._cur.total)
        if deduction > 0:
            # which is the granted grade
            deduction = min(self._cur.total, self._cur.total - deduction)
        elif deduction < 0:
            deduction = -deduction
        elif deduction == 0 and deducted:
            deduction = self._cur.total
        self._cur.deduct(deduction, remark)        
        self.records.append(self._cur)
        pass
    
    def rewind(self):
        try:
            self._rewind_one = self.records[self._rewind_index]
            self._rewind_index -= 1
        except IndexError:
            self._rewind_one = None
    
    def save_rewound(self, deduction, remark, deducted=True):        
        deduction = self._clamp(deduction, self._rewind_one.total)
        try:
            if deduction > 0:
                # which is the granted grade
                deduction = min(self._rewind_one.total, 
                                self._rewind_one.total - deduction)
            elif deduction < 0:
                deduction = -deduction
            elif deduction == 0 and deducted:
                deduction = self._rewind_one.total
            self._rewind_one.deduct(deduction, remark)
        except AttributeError as ae:
            if self._rewind_one != None: raise ae
        finally:
            self.end_rewind()
    
    def save_grumble(self, deduction, remark):
        grumble = _Grumble(deduction, remark)
        self.grumble_list.append(grumble)
        self.records.insert(self._rewind_index, grumble)
        pass
    
    def end_rewind(self):
        self._rewind_index = -1
        self._rewind_one = None
        pass
    
    def ask(self):
        try:
            return self._cur.ask()
        except AttributeError as ae:
            if self._cur != None: raise ae
            else: return "there is no more coming for this submission."
    
    def ask_rewound(self):
        try:
            return "%s\nYou put: \"%s\"" %\
                    (self._rewind_one.ask(), self._rewind_one.brief_answer())
        except AttributeError as ae:
            if self._rewind_one != None: raise ae
            else: return "There is no more to rewind."
    
    def wrap_up(self):
        self.remark = ""
        self.total = 0
        for section in self.sections:
            self.remark += section.report()+'\n'
            self.total += section.total
        if self.overall.report() != "":
            self.remark += "---(-%d)Overall---\n%s\n" %\
                (self.overall.deduction, self.overall.report())            
        self.total -= self.overall.deduction
        if len(self.grumble_list) != 0:
            grumble_demage = 0
            grumble_words = ""
            for grumble in self.grumble_list:
                grumble_words += grumble.answer() +'\n'
                grumble_demage += grumble.deduction
            self.total -= grumble_demage
            self.remark += "---(-%d)Supplemental remarks---\n" % grumble_demage
            self.remark += grumble_words
        self.total = max(self.total, 0)
        
    def brief(self):
        return "total: %d/%d" % (self.total, self.fullmark)
    
    def report(self):
        return "%s------\n%s\n\n%s" % (self.remark, self.brief(), self.signature)
    

def _judge_answer(answer):
    answer = answer.lower()
    yes_set = ['', 'y', 'yes', 'si', 'oui', 'ja']
    no_set = ['n', 'no', 'non', 'nein']
    if answer in yes_set:
        is_answered, grade_next = True, True
    elif answer in no_set:
        is_answered, grade_next = True, False
    else:
        is_answered, grade_next = False, None
    return is_answered, grade_next

def _help():
    print "to start grading: python grade.py <path of rubrics yaml file>"
    rules = """when grading:
input pattern: [<command>][<deduction>][<remark>]
for affirmative answer: -a or just nothing
for deducting points: -d <point>[<remark>]
    for positive point, it is the given score
    for negative point, it is the deduction
    for zero, it means deducting all
for rewinding previous record: -r
    keep on rewinding: -r
    stop rewinding and get back: -a or just nothing
    correct on rewound record:
        still to deduct: -d <point>[<remark>]
        cancel that deduction: -c
to grumble (and deduct if needed): -g[<deduction>]<remark>
"""
    print rules
    return
    
def _handle_args(args):
    path = None
    if len(args) <= 1:
        _help()
        exit()
    else:
        path = args[1]
    return path

def _copy_to_clipboard(content):
    cb.copy(content)
    
class _Grade_Cmd:
    UNRECOGNIZED = -1
    REJECT = 0
    APPROVE = 1
    REWIND = 2
    GRUMBLE = 3
    CANCEL = 4
    _cmd_dic = {"": APPROVE, # silence approval
                "-a": APPROVE,
                "-d": REJECT,
                "-r": REWIND,
                "-g": GRUMBLE,
                "-c": CANCEL
                }
    _cmd_pattern = r'(-[a-zA-Z]+)?\s*(-?\d+)?\s*(.+)?'
    @staticmethod
    def parce_cmd(in_str):
        cmd = _Grade_Cmd.UNRECOGNIZED
        deduction = 0
        remark = ""
        in_str = in_str.strip()
        try:
            cmd, deduction, remark = re.match(_Grade_Cmd._cmd_pattern, in_str).group(1,2,3)
        except TypeError: # likely encountering None
            pass
        else:
            try:
                cmd = _Grade_Cmd._cmd_dic[cmd]
            except KeyError:
                if cmd == None:
                    if deduction == None:
                        cmd = _Grade_Cmd.APPROVE
                    else:
                        cmd = _Grade_Cmd.REJECT
                else:
                    cmd = _Grade_Cmd.UNRECOGNIZED
            deduction = int(deduction) if deduction != None else 0
        finally:
           if deduction == None: deduction = 0 
           if remark == None: remark = ""
           if remark == "" and cmd == _Grade_Cmd.GRUMBLE:
               cmd = _Grade_Cmd.UNRECOGNIZED # cannot have empty grumbles
#         print "cmd:%d, ded:%d, remark:%s" %(cmd, deduction, remark)
        return cmd, deduction, remark
    
def _main():
    path = _handle_args(sys.argv)
#     path = "../../rubrics/assignment3/assignment3rubric.yaml"
    grader = Grader(path)
    grade_next = True
    counter = 0
    start_time = time.localtime()
    while grade_next:
        subm_grad_time = time.time()
        while grader.has_next:
            settled = False
            grader.grade_next()
            while not settled:
                in_str = raw_input(grader.ask()+'\n')
                cmd, deduction, remark = _Grade_Cmd.parce_cmd(in_str)
                if cmd == _Grade_Cmd.APPROVE:
                    grader.save_current(0, remark)
                    settled = True
                elif cmd == _Grade_Cmd.REJECT:                    
                    grader.save_current(deduction, remark, True)
                    settled = True
                elif cmd == _Grade_Cmd.REWIND:
                    rewinding = True
                    just_grumbled = False
                    while rewinding:
                        if not just_grumbled:
                            grader.rewind()
                        else:
                            just_grumbled = False # reset the grumble state
                        in_str = raw_input(grader.ask_rewound()+'\n')
                        cmd, deduction, remark = _Grade_Cmd.parce_cmd(in_str)
                        if cmd == _Grade_Cmd.APPROVE:
                            rewinding = False # cancel rewinding
                        elif cmd == _Grade_Cmd.REJECT:
                            grader.save_rewound(deduction, remark)
                            rewinding = False # rewinding complete
                        elif cmd == _Grade_Cmd.CANCEL:
                            grader.save_rewound(deduction, remark, False)
                            rewinding = False # rewinding complete
                        elif cmd == _Grade_Cmd.REWIND:
                            continue # has not rewound to wanted place
                        elif cmd == _Grade_Cmd.GRUMBLE:
                            grader.save_grumble(deduction, remark)
                            just_grumbled = True
                        else: # unrecognized cmd
                            print "what? try again?"
                            # take as a meaningless one
                            # so don't save
                            just_grumbled = True                            
                    # end of rewinding
                    grader.end_rewind()
                    # things are unsettled, the current one is
                    # ungraded, do nothing to continue
                elif cmd == _Grade_Cmd.GRUMBLE:
                    grader.save_grumble(deduction, remark)
                    # finish grumbling and keep on grading
                else:
                    print "what did you say? try again?"
                    # take as a meaningless one
                    # so don't save
            # end of while not settled
            # finished grading the one on hand
        # end of while grader.has_next()
        # finished grading all
        grader.wrap_up() 
        _copy_to_clipboard(grader.report())
        print grader.brief()
        is_answered = False
        subm_grad_time = int(time.time() - subm_grad_time)
        print "time taken: %d sec" % subm_grad_time
        print "detailed feedback is copied to clipboard."
        while not is_answered:
            answer = raw_input("Grade a new one ([y]/n)?")
            is_answered, grade_next = _judge_answer(answer) 
        grader.reset()
        counter += 1
    print "Have graded %d assignment(s)" % counter
    print "Started from %s" % time.strftime("%H:%M:%S", start_time)
    return

if __name__ == "__main__":
    _main()
