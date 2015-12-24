import sys
import ctypes
    
 
from PySide.QtGui import *
from PySide.QtCore import *
 
 
qt_app = QApplication(sys.argv)
 
class MyWidget(QWidget):
    ''' An example of PySide absolute positioning; the main window
        inherits from QWidget, a convenient widget for an empty window. '''
    def __init__(self):
        # Initialize the object as a QWidget
        QWidget.__init__(self)
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


