from PyQt5.QtWidgets import QDialog, QVBoxLayout, QLabel, QLineEdit, QPushButton, QFormLayout

class RegisterUserDialog(QDialog):
    def __init__(self, uid):
        super().__init__()
        self.setWindowTitle("New User Registration")
        self.setGeometry(400, 200, 300, 200)

        self.uid = uid
        self.name_input = QLineEdit()
        self.balance_input = QLineEdit()

        self.layout = QVBoxLayout()

        form_layout = QFormLayout()
        form_layout.addRow(QLabel(f"UID: {self.uid}"))
        form_layout.addRow("Name:", self.name_input)
        form_layout.addRow("Initial Balance (₹):", self.balance_input)

        self.register_btn = QPushButton("Register")
        self.register_btn.clicked.connect(self.accept)

        self.layout.addLayout(form_layout)
        self.layout.addWidget(self.register_btn)
        self.setLayout(self.layout)

    def get_data(self):
        return self.name_input.text(), self.balance_input.text()