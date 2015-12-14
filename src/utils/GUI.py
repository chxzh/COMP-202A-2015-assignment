import sys
import ctypes
    
 
from PySide.QtGui import *
from PySide.QtCore import *
 
 
qt_app = QApplication(sys.argv)
 
class AbsolutePositioningExample(QWidget):
    ''' An example of PySide absolute positioning; the main window
        inherits from QWidget, a convenient widget for an empty window. '''
    def __init__(self):
        # Initialize the object as a QWidget
        QWidget.__init__(self)
 
        # We have to set the size of the main window
        # ourselves, since we control the entire layout
        self.setMinimumSize(400, 185)
        self.setMaximumWidth(600)
        self._margin_size = 5
        
        self.setWindowTitle('Grader 202')
        # getting resolution of primary monitor
        sc_width, sc_heigth = ctypes.windll.user32.GetSystemMetrics(0),\
                ctypes.windll.user32.GetSystemMetrics(1)
 
        self.resize(480, 600)
        # Create the controls with this object as their parent and set
        # their position individually; each row is a label followed by
        # another control
        self.question_header = QLabel("Score Point",self)
#         self.question_header.resize(self.get_widget_full_width(), 5)
        self.question_header.move(self._margin_size, self._margin_size)
        
        self.question_field = QTextEdit("Where there will be a score point hint", self)
        self.question_field.resize(self.get_widget_full_width(), 50)
        self.question_field.move(self._margin_size, self._margin_size + self.question_header.height())
        self.question_field.setReadOnly(True)
        
        self.score_field = QLineEdit(self)
        self.score_field.move(self._margin_size, 100)
        self.score_field.setValidator(QIntValidator(-10, 10, self.score_field))
        return
    
        # Label for the salutation chooser
        self.salutation_lbl = QLabel('Salutation:', self)
        self.salutation_lbl.move(5, 5) # offset the first control 5px
                                       # from top and left
        self.salutations = ['Ahoy',
                            'Good day',
                            'Hello',
                            'Heyo',
                            'Hi',
                            'Salutations',
                            'Wassup',
                            'Yo']
        # Create and fill the combo box to choose the salutation
        self.salutation = QComboBox(self)
        self.salutation.addItems(self.salutations)
 
        # Allow 100px for the label and 5px each for borders at the
        # far left, between the label and the combobox, and at the far
        # right
        self.salutation.setMinimumWidth(285)
        # Place it five pixels to the right of the end of the label
        self.salutation.move(110, 5)
 
        # The label for the recipient control
        self.recipient_lbl = QLabel('Recipient:', self)
        # 5 pixel indent, 25 pixels lower than last pair of widgets
        self.recipient_lbl.move(5, 30)
 
        # The recipient control is an entry textbox
        self.recipient = QLineEdit(self)
        # Add some ghost text to indicate what sort of thing to enter
        self.recipient.setPlaceholderText("e.g. 'world' or 'Matey'")
        # Same width as the salutation
        self.recipient.setMinimumWidth(285)
        # Same indent as salutation but 25 pixels lower
        self.recipient.move(110, 30)
 
        # The label for the greeting widget
        self.greeting_lbl = QLabel('Greeting:', self)
        # Same indent as the others, but 45 pixels lower so it has
        # physical separation, indicating difference of function
        self.greeting_lbl.move(5, 75)
 
        # The greeting widget is also a label
        self.greeting = QLabel('', self)
        # Same indent as the other controls
        self.greeting.move(110, 75)
 
        # The build button is a push button
        self.build_button = QPushButton('Build Greeting', self)
 
        # Place it at the bottom right, narrower than
        # the other interactive widgets
        self.build_button.setMinimumWidth(145)
        self.build_button.move(250, 150)
        
    def get_widget_full_width(self):
        return self.width() - 2*self._margin_size
    
    def run(self):
        # Show the form
        self.show()
        # Run the Qt application
        qt_app.exec_()
 
# Create an instance of the application window and run it
app = AbsolutePositioningExample()
app.run()