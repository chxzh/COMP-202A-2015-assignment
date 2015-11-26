import clipboard as cb
import time, sys, re
from boto.mturk.question import AnswerSpecification
import yaml
try:
    from yaml import CLoader as Loader
except ImportError:
    from yaml import Loader as Loader

class Grader:
    def __init__(self, rubric_path):
        with open(rubric_path, 'r') as rubric_file:
            rubric = yaml.load(rubric_file, Loader)
        try:
            # load compulsory components
            self.fullmark = rubric.pop("_fullmark")
            self.overall = rubric.pop("_overall")
        except KeyError as err:
            raise Exception("cannot have root component %s in rubric file %d" % 
                            (err.args[0], rubric_path))
        # load optional components
        self.signature = self._load_optional(rubric, "_signature", '')
        self.sections = rubric
        self.remark = ""
        self.score = self.fullmark
    
    def _load_optional(self, dic, key, backup):
        try:
            return dic.pop(key)
        except KeyError:
            return backup
    
    def has_next(self):
        pass
    
    def grade_next(self):
        pass
    
    def save_current(self):
        pass
    
    def rewind(self):
        pass
    
    def save_rewound(self):
        pass
    
    def save_grumble(self):
        pass
    
    def end_rewind(self):
        pass
    
    def ask(self):
        pass
    
    def ask_rewound(self):
        pass
    
    def reset(self):
        self.remark = ""
        self.score = self.fullmark
        
    def _wrap_up(self):
        pass
    
    def _rollback(self):
        pass
    
    def brief(self):
        pass
    
    def report(self):
        pass
    
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
            self.deduction = deduction
            self.remark = remark
            self.deducted = True
        
        def ask(self):
            return "(%d pt)Did %s %s?" % \
                    (self.total, self.subject, self.action)
        
        def answer(self):
            response = ""
            if deducted:
                response = "(-%d) %s didn't %s." % \
                        (self.deduction, self.subject, self.action)
            else:
                response = "%s did %s." % (self.subject, self.action)
            if self.remark != "":
                response += " %s" % (self.remark)
            return response
         
        def brief_answer(self):
            response = ''
            if deducted:
                response = '(-%d) no.' % self.deduction
            else:
                response = "yes."            
            if self.remark != "":
                response += " %s" % self.remark
            return response
    
    
    class _Subsection():
        def __init__(self, subject, subsec_dic):
            self.subject = subject
            self.total = subsec_dic.pop("_total")
            self.fullmark = self.total
            self.score_points = [_Score_point(subject, action, total)\
                    for action, total in subsec_dic["deduction"].iteritems()]
        
        def reset(self):
            self.total = self.fullmark
            for score_point in self.score_points:
                score_point.reset()
        
        def report(self):
            report = ""
            for score_point in self.score_points:
                if score_point.deducted:
                    self.total -= score_point.deduction
                    report += "\n- %s" % (score_point.answer)
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
                    report += "\n- %s" % (score_point.answer)
            self.total = max(0, self.total)
            if self.deduction != 0:
                report = "(-%d) overall:%s" % \
                        (self.deduction, report)
            else:
                report = ""
            return report
                    
                    
    class _Section():
        def __init__(self, title, sec_dic):
            self.title = title
            self.total = sec_dic.pop("_total")
            self.fullmark = self.total
            try:
                self.overall = _Overall_Section(self.title,
                                           sec_dic.pop("_overall"))
            except KeyError:
                self.overall = _Overall_Section(self.title)
            self.subsections = [_Subsection(subject, subsec_dic)\
                                for subject, subsec_dic in sec_dic.iteritems()]
        
        def reset(self):
            self.total = self.fullmark
            for subsec in self.subsection:
                subsec.reset()
            self.overall.reset()
        
        def report(self):
            report = ""
            self.total = 0
            for subsec in self.subsections:
                report += "\n%s" % subsec.report
                self.total += subsec.total
            if self.overall.deduction != 0:
                report += "\n%s" % self.overall.report()
                self.total -= self.overall.deduction
            self.total = max(self.total, 0)
            report = "---%s (%d/%d)---\n%s" % \
                    (self.title, self.total, self.fullmark, report)
            return report

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
    print "grade.py <path of rubrics yaml file>"
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
    _cmd_dic = {"": APPROVE, # silence approval
                None: APPROVE,
                "-a": APPROVE,
                "-d": REJECT,
                "-r": REWIND,
                "-g": GRUMBLE
                }
    _cmd_pattern = r'(-\w*)?\s*(-?\d+)?\s*(.+)?'
    @staticmethod
    def parce_cmd(in_str):
        cmd = UNRECOGNIZED
        deduction = 0
        remark = ""
        in_str = in_str.strip()
        try:
            cmd, deduction, remark = re.match(_cmd_pattern, in_str).group(1,2,3)
        except TypeError: # likely encountering None
            pass
        else:
            try:  
                cmd = _cmd_dic[cmd]
            except KeyError:
                cmd = UNRECOGNIZED
            deduction = int(deduction) if deduction != None else 0
        return cmd, deduction, remark
    
def _main():
    path = _handle_args(sys.argv)
    grader = Grader(path)
    grade_next = True
    counter = 0
    start_time = time.localtime()
    while grade_next:
        subm_grad_time = time.time()
        while grader.has_next():
            settled = False
            grader.grade_next()
            while not settled:
                in_str = raw_input(grader.ask()+'\n')
                cmd, deduction, remark = _Grade_Cmd.parce_cmd(in_str)
                if cmd == _Grade_Cmd.APPROVE or cmd == _Grade_Cmd.REJECT:
                    grader.save_current(deduction, remark)
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
                            grade.save_rewound(deduction, remark)
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
        _copy_to_clipboard(grader.report())
        print grader.brief()
        is_answered = False
        subm_grad_time = int(time.time() - subm_grad_time)
        print "time taken: %d sec" % subm_grad_time
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
