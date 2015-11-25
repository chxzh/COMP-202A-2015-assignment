import clipboard as cb
import time, sys
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
            print "cannot have root component %s in rubric file %d" % (err.args[0], rubric_path)
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
    
    def reset(self):
        self.remark = ""
        self.score = self.fullmark
            
    def fill_clipboard(self):
        pass
    
    def grade(self):
        pass
    
    def brief(self):
        pass

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
    
def _main():
    path = _handle_args(sys.argv)
    grader = Grader(path)
    grade_next = True
    counter = 0
    start_time = time.localtime()
    while grade_next:
        subm_grad_time = time.time()
        grader.grade()
        grader.fill_clipboard()
        grader.brief()
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