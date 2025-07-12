import os
import subprocess
import sys
import time
from pathlib import Path

import requests
from PyQt6.QtWidgets import (QApplication, QMainWindow, QVBoxLayout, QHBoxLayout,
                             QLineEdit, QPushButton, QLabel, QWidget, QDateEdit,
                             QMessageBox, QGroupBox, QFormLayout, QComboBox)
from PyQt6.QtCore import Qt, QDate
from PyQt6.QtGui import QFont, QPalette, QColor


class CreditApp(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setup_ui()
        self.setStyleSheet("""
            QMainWindow {
                background-color: #f5f7fa;
            }
            QGroupBox {
                border: 1px solid #d1d5db;
                border-radius: 6px;
                margin-top: 10px;
                padding-top: 15px;
                font-weight: bold;
                color: #374151;
            }
            QLabel {
                color: #4b5563;
                font-size: 14px;
            }
            QLineEdit, QDateEdit, QComboBox {
                border: 1px solid #d1d5db;
                border-radius: 4px;
                padding: 5px;
                min-height: 25px;
                font-size: 14px;
            }
            QPushButton {
                background-color: #4f46e5;
                color: white;
                border: none;
                border-radius: 4px;
                padding: 8px 16px;
                font-weight: bold;
                font-size: 14px;
            }
            QPushButton:hover {
                background-color: #4338ca;
            }
        """)

    def setup_ui(self):
        self.setWindowTitle("Кредитный калькулятор")
        self.setMinimumSize(500, 600)

        # Главный контейнер
        main_widget = QWidget()
        main_layout = QVBoxLayout()
        main_layout.setContentsMargins(20, 20, 20, 20)
        main_layout.setSpacing(15)

        # Заголовок
        title = QLabel("Оформление кредитной заявки")
        title.setFont(QFont("Arial", 16, QFont.Weight.Bold))
        title.setAlignment(Qt.AlignmentFlag.AlignCenter)
        main_layout.addWidget(title)

        # Группа "Данные клиента"
        client_group = QGroupBox("Данные клиента")
        client_layout = QFormLayout()
        client_layout.setVerticalSpacing(10)

        self.full_name_input = QLineEdit()
        self.passport_input = QLineEdit()
        self.birth_date_input = QDateEdit(calendarPopup=True)
        self.birth_date_input.setDisplayFormat("dd.MM.yyyy")
        self.birth_date_input.setDate(QDate(1990, 1, 1))

        client_layout.addRow("ФИО:", self.full_name_input)
        client_layout.addRow("Паспорт:", self.passport_input)
        client_layout.addRow("Дата рождения:", self.birth_date_input)
        client_group.setLayout(client_layout)
        main_layout.addWidget(client_group)

        # Группа "Параметры кредита"
        credit_group = QGroupBox("Параметры кредита")
        credit_layout = QFormLayout()
        credit_layout.setVerticalSpacing(10)

        self.amount_input = QLineEdit()
        self.amount_input.setPlaceholderText("руб.")
        self.term_combo = QComboBox()
        self.term_combo.addItems(["12 месяцев", "24 месяца", "36 месяцев"])
        self.initial_payment_input = QLineEdit()
        self.initial_payment_input.setPlaceholderText("руб.")
        self.income_input = QLineEdit()
        self.income_input.setPlaceholderText("руб./мес")
        self.experience_combo = QComboBox()
        self.experience_combo.addItems(["1", "3", "5", "10"])  # Только числа
        self.experience_combo_label = QLabel("лет")

        credit_layout.addRow("Сумма кредита:", self.amount_input)
        credit_layout.addRow("Срок:", self.term_combo)
        credit_layout.addRow("Первоначальный взнос:", self.initial_payment_input)
        credit_layout.addRow("Доход:", self.income_input)
        exp_layout = QHBoxLayout()
        exp_layout.addWidget(self.experience_combo)
        exp_layout.addWidget(self.experience_combo_label)
        exp_layout.addStretch()  # Чтобы надпись не растягивалась
        credit_layout.addRow("Стаж работы:", exp_layout)
        credit_group.setLayout(credit_layout)
        main_layout.addWidget(credit_group)

        # Кнопка отправки
        submit_btn = QPushButton("Рассчитать условия")
        submit_btn.clicked.connect(self.submit_application)
        main_layout.addWidget(submit_btn)

        # Группа "Результат"
        self.result_group = QGroupBox("Результат рассмотрения")
        self.result_group.setVisible(False)
        result_layout = QVBoxLayout()

        self.result_label = QLabel()
        self.result_label.setWordWrap(True)
        self.result_label.setFont(QFont("Arial", 12))

        self.details_label = QLabel()
        self.details_label.setWordWrap(True)
        self.details_label.setStyleSheet("color: #4b5563; font-size: 13px;")

        result_layout.addWidget(self.result_label)
        result_layout.addWidget(self.details_label)
        self.result_group.setLayout(result_layout)
        main_layout.addWidget(self.result_group)

        # Завершение настройки
        main_widget.setLayout(main_layout)
        self.setCentralWidget(main_widget)

    def submit_application(self):
        # Проверяем заполнение всех обязательных полей
        if not all([
            self.full_name_input.text().strip(),
            self.passport_input.text().strip(),
            self.amount_input.text().strip(),
            self.initial_payment_input.text().strip(),
            self.income_input.text().strip()
        ]):
            self.show_error("Пожалуйста, заполните все обязательные поля")
            return

        try:
            # Преобразуем числовые значения с проверкой
            amount = self.validate_number(self.amount_input.text(), "Сумма кредита")
            initial_payment = self.validate_number(self.initial_payment_input.text(), "Первоначальный взнос")
            income = self.validate_number(self.income_input.text(), "Доход")

            # Проверяем, что взнос не больше суммы кредита
            if initial_payment >= amount:
                self.show_error("Первоначальный взнос не может быть больше суммы кредита")
                return

            data = {
                "fullName": self.full_name_input.text(),
                "passportNumber": self.passport_input.text(),
                "birthDate": self.birth_date_input.date().toString("yyyy-MM-dd"),
                "amount": amount,
                "term": int(self.term_combo.currentText().split()[0]),
                "initialPayment": initial_payment,
                "declaredIncome": income,
                "employmentYears": int(self.experience_combo.currentText().split()[0])
            }

            response = requests.post(
                "http://localhost:8080/api/forms",
                json=data,
                headers={"Content-Type": "application/json"},
                timeout=5  # Таймаут 5 секунд
            )

            if response.status_code == 200:
                self.show_result(response.json())
            else:
                self.show_error(f"Ошибка сервера: {response.text}")

        except ValueError as e:
            self.show_error(str(e))
        except requests.exceptions.RequestException as e:
            self.show_error(f"Ошибка соединения с сервером: {str(e)}")

    def validate_number(self, value, field_name):
        """Проверяет и преобразует числовое значение"""
        try:
            num = float(value.replace(" ", "").replace(",", "."))
            if num <= 0:
                raise ValueError(f"{field_name} должно быть положительным числом")
            return num
        except ValueError:
            raise ValueError(f"Некорректное значение для {field_name}. Введите число")

    def show_result(self, result):
        self.result_group.setVisible(True)

        if result.get("status") == "APPROVED":
            self.result_label.setText("✅ Кредит одобрен!")
            self.result_label.setStyleSheet("color: #10b981; font-weight: bold;")

            details = [
                f"<b>Ставка:</b> {result.get('interestRate', 0) * 100}%",
                f"<b>Платёж/мес:</b> {result.get('monthlyPayment', 0):,.2f} руб.",
                f"<b>Переплата:</b> {result.get('totalOverpayment', 0):,.2f} руб."
            ]

            self.details_label.setText("<br>".join(details))
        else:
            self.result_label.setText("❌ Отказ")
            self.result_label.setStyleSheet("color: #ef4444; font-weight: bold;")
            self.details_label.setText("")

    def show_error(self, message):
        msg = QMessageBox(self)
        msg.setIcon(QMessageBox.Icon.Warning)
        msg.setWindowTitle("Ошибка")
        msg.setText(message)
        msg.exec()


def start_java_server():
    jar_path = str(Path(__file__).parent / "bank-server.jar")

    if not os.path.exists(jar_path):
        QMessageBox.critical(None, "Ошибка", "Файл bank-server.jar не найден!")
    return None  # Возвращаем None вместо exit

    # Запускаем сервер и возвращаем процесс
    return subprocess.Popen(
        ["java", "-jar", jar_path],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        creationflags=subprocess.CREATE_NO_WINDOW  # Скрываем консоль
    )


def wait_for_server():
    """Ждем пока сервер станет доступен"""
    for _ in range(30):  # 30 попыток с интервалом 1 секунда
        try:
            response = requests.get("http://localhost:8080/api/health", timeout=1)
            if response.status_code == 200:
                return True
        except:
            time.sleep(1)
    return False


if __name__ == "__main__":
    # 1. Сначала запускаем сервер
    server_process = start_java_server()
    if server_process is None:
        sys.exit(1)

    # 2. Ждем его готовности
    if not wait_for_server():
        QMessageBox.critical(None, "Ошибка", "Сервер не запустился за 30 секунд!")
        server_process.terminate()
        sys.exit(1)

    # 3. Только потом запускаем GUI
    app = QApplication(sys.argv)
    window = CreditApp()
    window.show()

    # 4. При закрытии убиваем сервер
    ret = app.exec()
    server_process.terminate()
    sys.exit(ret)
