import sys
    
 
from PySide.QtGui import *
from PySide.QtCore import *
 
 
qt_app = QApplication(sys.argv)
 
class MyWidget(QWidget):
    ''' An example of PySide absolute positioning; the main window
        inherits from QWidget, a convenient widget for an empty window. '''
    def __init__(self):
        # Initialize the object as a QWidget
        QWidget.__init__(self)
<<<<<<< HEAD
 
        # We have to set the size of the main window
        # ourselves, since we control the entire layout
        self.setMinimumSize(400, 185)
        self.setMaximumWidth(600)
        self.resize(480, 600)
        
        self.setWindowTitle('Grader 202')
 
        # Create the controls with this object as their parent and set
        # their position individually; each row is a label followed by
        # another control
 
        # Label for the salutation chooser
        self.question
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
 
=======
        img_path = "C:\\Users\\cxz\\Pictures\\shipping.png"
        
        scn = QGraphicsScene()
        view = QGraphicsView(scn,self)
         
        pixmap = QPixmap(img_path)
        gfxPixItem = scn.addPixmap(pixmap)
        item = scn.addPixmap(pixmap)
        view.fitInView(item)
        
        view.show()
        
#         self.label.setPixmap(img)
#         self.label.show()
        
    
>>>>>>> origin/master
    def run(self):
        # Show the form
        self.show()
        # Run the Qt application
        qt_app.exec_()
 
# Create an instance of the application window and run it
app = MyWidget()
app.run()


# scn = QGraphicsScene()
# view = QGraphicsView(scn)
# 
# pixmap = QPixmap("C:\\Users\\cxz\\Pictures\\shipping.png")
# gfxPixItem = scn.addPixmap(pixmap)
# 
# view.fitInView(gfxPixItem)
# view.show()
# 
# sys.exit(qt_app.exec_())


