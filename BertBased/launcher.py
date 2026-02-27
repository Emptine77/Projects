import tkinter as tk
from tkinter import ttk, filedialog, messagebox
import threading
import subprocess
import sys
import importlib
import torch
import os
import traceback
import re # Добавлено для парсинга логов

import config
import data_utils
import model_pretrained
import model_scratch
from transformers import AutoTokenizer, BertTokenizer

try:
    import docx
except ImportError:
    docx = None

try:
    import PyPDF2
except ImportError:
    PyPDF2 = None

class ML_Launcher:
    def __init__(self, root):
        self.root = root
        self.root.title("Transformer Studio")
        self.root.geometry("1100x900") # Чуть увеличили окно для новых элементов
        
        style = ttk.Style()
        style.theme_use('clam')
        
        style.configure("Big.TButton", font=("Arial", 11, "bold"), padding=10)
        style.configure("Stop.TButton", font=("Arial", 11, "bold"), padding=10, foreground="red")
        style.configure("Header.TLabel", font=("Arial", 10, "bold"), foreground="#333")
        style.configure("Metric.TLabel", font=("Consolas", 12, "bold"), foreground="#1565c0")
        
        self.inference_model = None
        self.inference_tokenizer = None
        self.inference_id_to_tag = None
        self.inference_probs = None
        self.device = None
        self.training_process = None # Ссылка на процесс обучения
        
        self.vars = {}
        self.init_variables()

        main_frame = ttk.Frame(root, padding="10")
        main_frame.pack(fill=tk.BOTH, expand=True)

        self.notebook = ttk.Notebook(main_frame)
        self.notebook.pack(fill=tk.BOTH, expand=True, pady=(0, 10))

        self.tab_predict = ttk.Frame(self.notebook, padding=15)
        self.tab_data = ttk.Frame(self.notebook, padding=15)
        self.tab_model = ttk.Frame(self.notebook, padding=15)
        self.tab_train = ttk.Frame(self.notebook, padding=15)

        self.notebook.add(self.tab_predict, text="Інференс")
        self.notebook.add(self.tab_data, text="Дані та Файли")
        self.notebook.add(self.tab_model, text="Архітектура Моделі")
        self.notebook.add(self.tab_train, text="Навчання")

        self.build_predict_tab()
        self.build_data_tab()
        self.build_model_tab()
        self.build_train_tab()

        log_labelframe = ttk.LabelFrame(main_frame, text="Лог виконання")
        log_labelframe.pack(fill=tk.BOTH, expand=True, pady=5)
        
        self.console_text = tk.Text(log_labelframe, height=8, bg="#212121", fg="#00e676", 
                                    font=("Consolas", 9), relief=tk.FLAT)
        self.console_text.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        
        scrollbar = ttk.Scrollbar(log_labelframe, command=self.console_text.yview)
        scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
        self.console_text['yscrollcommand'] = scrollbar.set

    def init_variables(self):
        """Ініціалізація змінних з config.py + Нові змінні"""
        self.vars['USE_PRETRAINED_MODEL'] = tk.BooleanVar(value=config.USE_PRETRAINED_MODEL)
        self.vars['DATA_FILE'] = tk.StringVar(value=config.DATA_FILE)
        self.vars['TAG_MAP_FILE'] = tk.StringVar(value=config.TAG_MAP_FILE)
        self.vars['BEST_MODEL_PATH'] = tk.StringVar(value=config.BEST_MODEL_PATH)
        
        self.vars['TEXT_COLUMN_NAME'] = tk.StringVar(value=config.TEXT_COLUMN_NAME)
        self.vars['TAG_COLUMN_NAME'] = tk.StringVar(value=config.TAG_COLUMN_NAME)
        self.vars['PARSED_TAG_COLUMN'] = tk.StringVar(value=config.PARSED_TAG_COLUMN)
        self.vars['FILTERED_TAG_COLUMN'] = tk.StringVar(value=config.FILTERED_TAG_COLUMN)
        self.vars['MIN_TAG_FREQUENCY'] = tk.IntVar(value=config.MIN_TAG_FREQUENCY)
        
        self.vars['MODEL_NAME'] = tk.StringVar(value=config.MODEL_NAME)
        self.vars['MAX_LENGTH'] = tk.IntVar(value=config.MAX_LENGTH)
        self.vars['BATCH_SIZE'] = tk.IntVar(value=config.BATCH_SIZE)
        
        self.vars['D_MODEL'] = tk.IntVar(value=config.D_MODEL)
        self.vars['DROPOUT_P'] = tk.DoubleVar(value=config.DROPOUT_P)
        self.vars['N_HEADS'] = tk.IntVar(value=config.N_HEADS)
        self.vars['N_LAYERS'] = tk.IntVar(value=config.N_LAYERS)
        self.vars['FFN_HID_DIM'] = tk.IntVar(value=config.FFN_HID_DIM)
        
        self.vars['NUM_EPOCHS'] = tk.IntVar(value=config.NUM_EPOCHS)
        self.vars['LEARNING_RATE'] = tk.DoubleVar(value=config.LEARNING_RATE)
        self.vars['VALIDATION_SPLIT'] = tk.DoubleVar(value=config.VALIDATION_SPLIT)
        self.vars['SEED'] = tk.IntVar(value=config.SEED)
        self.vars['POS_WEIGHT'] = tk.DoubleVar(value=config.POS_WEIGHT)
        
        self.vars['PREDICT_THRESHOLD'] = tk.DoubleVar(value=0.5)

        # НОВИЕ ПЕРЕМЕННЫЕ
        self.vars['RESUME_TRAINING'] = tk.BooleanVar(value=False)
        self.vars['FREEZE_ENCODER'] = tk.BooleanVar(value=getattr(config, 'FREEZE_ENCODER', False))
        self.vars['GRADIENT_CHECKPOINTING'] = tk.BooleanVar(value=getattr(config, 'GRADIENT_CHECKPOINTING', True))

    def set_recursive_state(self, widget, state):
        try: widget.configure(state=state)
        except tk.TclError: pass
        for child in widget.winfo_children():
            self.set_recursive_state(child, state)

    def build_predict_tab(self):
        # ... (Код вкладки Predict остается без изменений) ...
        paned = ttk.PanedWindow(self.tab_predict, orient=tk.HORIZONTAL)
        paned.pack(fill=tk.BOTH, expand=True)

        left_frame = ttk.Frame(paned)
        right_frame = ttk.Frame(paned)
        paned.add(left_frame, weight=1)
        paned.add(right_frame, weight=3)

        model_frame = ttk.LabelFrame(left_frame, text="Управління")
        model_frame.pack(fill=tk.X, padx=5, pady=5)

        ttk.Label(model_frame, text="Файл моделі (.bin):").pack(anchor="w", padx=5)
        path_frame = ttk.Frame(model_frame)
        path_frame.pack(fill=tk.X, padx=5, pady=2)
        ttk.Entry(path_frame, textvariable=self.vars['BEST_MODEL_PATH']).pack(side=tk.LEFT, fill=tk.X, expand=True)
        ttk.Button(path_frame, text="📂", width=3, command=lambda: self.browse_file(self.vars['BEST_MODEL_PATH'])).pack(side=tk.LEFT, padx=2)
        
        self.btn_load_model = ttk.Button(model_frame, text="Завантажити в пам'ять", command=self.load_inference_model)
        self.btn_load_model.pack(fill=tk.X, padx=5, pady=10)
        self.lbl_model_status = ttk.Label(model_frame, text="Статус: Не завантажена", foreground="gray")
        self.lbl_model_status.pack(padx=5, pady=(0,5))

        sep = ttk.Separator(left_frame, orient='horizontal')
        sep.pack(fill=tk.X, padx=10, pady=10)

        files_frame = ttk.LabelFrame(left_frame, text="Джерело тексту")
        files_frame.pack(fill=tk.X, padx=5, pady=5)
        ttk.Button(files_frame, text="Завантажити TXT", command=lambda: self.load_text_file('txt')).pack(fill=tk.X, padx=5, pady=2)
        ttk.Button(files_frame, text="Завантажити DOCX", command=lambda: self.load_text_file('docx')).pack(fill=tk.X, padx=5, pady=2)
        ttk.Button(files_frame, text="Завантажити PDF", command=lambda: self.load_text_file('pdf')).pack(fill=tk.X, padx=5, pady=2)

        input_frame = ttk.LabelFrame(right_frame, text="Текст статті для передбачення")
        input_frame.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        self.txt_input = tk.Text(input_frame, height=10, font=("Arial", 10), wrap=tk.WORD)
        self.txt_input.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        
        action_frame = ttk.Frame(right_frame)
        action_frame.pack(fill=tk.X, padx=5, pady=5)
        self.btn_predict = ttk.Button(action_frame, text="ПЕРЕДБАЧИТИ ТЕГИ", style="Big.TButton", command=self.run_inference, state=tk.DISABLED)
        self.btn_predict.pack(side=tk.LEFT, fill=tk.X, expand=True, padx=(0, 10))
        
        slider_frame = ttk.LabelFrame(action_frame, text="Поріг (Threshold)")
        slider_frame.pack(side=tk.RIGHT, fill=tk.X, expand=True)
        self.lbl_threshold = ttk.Label(slider_frame, text="0.50", font=("Arial", 10, "bold"))
        self.lbl_threshold.pack(side=tk.RIGHT, padx=5)
        self.scale_threshold = ttk.Scale(slider_frame, from_=0.0, to=1.0, variable=self.vars['PREDICT_THRESHOLD'], command=self.on_slider_move)
        self.scale_threshold.pack(side=tk.LEFT, fill=tk.X, expand=True, padx=5)

        res_frame = ttk.LabelFrame(right_frame, text="Результат")
        res_frame.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        self.txt_output = tk.Text(res_frame, height=4, font=("Arial", 12, "bold"), fg="#2e7d32", bg="#f1f8e9", wrap=tk.WORD)
        self.txt_output.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)

    def build_data_tab(self):
        # ... (Код вкладки Data остается без изменений) ...
        self.tab_data.columnconfigure(0, weight=1)
        self.tab_data.columnconfigure(1, weight=1)

        files_frame = ttk.LabelFrame(self.tab_data, text="Шляхи до файлів")
        files_frame.grid(row=0, column=0, columnspan=2, sticky="ew", padx=5, pady=5)
        self.create_file_entry(files_frame, "Датасет (CSV):", self.vars['DATA_FILE'], 0)
        self.create_file_entry(files_frame, "Карта тегів (JSON):", self.vars['TAG_MAP_FILE'], 1)
        self.create_file_entry(files_frame, "Збереження моделі:", self.vars['BEST_MODEL_PATH'], 2)

        cols_frame = ttk.LabelFrame(self.tab_data, text="Структура CSV")
        cols_frame.grid(row=1, column=0, sticky="nsew", padx=5, pady=10)
        self.create_grid_entry(cols_frame, "Колонка з Текстом:", self.vars['TEXT_COLUMN_NAME'], 0)
        self.create_grid_entry(cols_frame, "Колонка з Тегами:", self.vars['TAG_COLUMN_NAME'], 1)
        ttk.Label(cols_frame, text="Службові колонки (авто):", foreground="gray").grid(row=2, column=0, columnspan=2, pady=(10,5))
        self.create_grid_entry(cols_frame, "Parsed List:", self.vars['PARSED_TAG_COLUMN'], 3)
        self.create_grid_entry(cols_frame, "Filtered List:", self.vars['FILTERED_TAG_COLUMN'], 4)

        filter_frame = ttk.LabelFrame(self.tab_data, text="Очищення даних")
        filter_frame.grid(row=1, column=1, sticky="nsew", padx=5, pady=10)
        ttk.Label(filter_frame, text="Мінімальна частота тегу:", font=("Arial", 10)).pack(pady=(20, 5))
        ttk.Entry(filter_frame, textvariable=self.vars['MIN_TAG_FREQUENCY'], width=10, font=("Arial", 12)).pack(pady=5)
        ttk.Label(filter_frame, text="Теги рідше цього числа видаляються.", foreground="gray").pack(pady=5)

    def build_model_tab(self):
        mode_frame = ttk.LabelFrame(self.tab_model, text="Тип архітектури")
        mode_frame.pack(fill=tk.X, padx=5, pady=5)
        
        chk = ttk.Checkbutton(mode_frame, text="Використовувати Pretrained Model (HuggingFace)", 
                              variable=self.vars['USE_PRETRAINED_MODEL'], command=self.toggle_model_ui)
        chk.pack(anchor="w", padx=10, pady=10)
        
        self.frame_pretrained = ttk.LabelFrame(self.tab_model, text="Налаштування Pretrained")
        self.frame_pretrained.pack(fill=tk.X, padx=5, pady=5)
        
        self.create_grid_entry(self.frame_pretrained, "HuggingFace Model ID:", self.vars['MODEL_NAME'], 0, width=50)
        
        # НОВЫЕ ГАЛОЧКИ ДЛЯ ОПТИМИЗАЦИИ VRAM
        opts_frame = ttk.Frame(self.frame_pretrained)
        opts_frame.grid(row=1, column=0, columnspan=2, sticky="w", padx=10, pady=5)
        
        ttk.Checkbutton(opts_frame, text="Заморозити Трансформер (навчати лише голову)", 
                        variable=self.vars['FREEZE_ENCODER']).pack(anchor="w")
        ttk.Checkbutton(opts_frame, text="Увімкнути Gradient Checkpointing (економія VRAM)", 
                        variable=self.vars['GRADIENT_CHECKPOINTING']).pack(anchor="w", pady=(5,0))

        self.frame_scratch = ttk.LabelFrame(self.tab_model, text="Налаштування 'From Scratch' (Трансформер з нуля)")
        self.frame_scratch.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)
        
        for i in range(3): self.frame_scratch.columnconfigure(i, weight=1)
        self.create_box_entry(self.frame_scratch, "D_MODEL (Size)", self.vars['D_MODEL'], 0, 0)
        self.create_box_entry(self.frame_scratch, "N_LAYERS", self.vars['N_LAYERS'], 0, 1)
        self.create_box_entry(self.frame_scratch, "N_HEADS", self.vars['N_HEADS'], 0, 2)
        self.create_box_entry(self.frame_scratch, "FFN Hidden Dim", self.vars['FFN_HID_DIM'], 1, 0)
        self.create_box_entry(self.frame_scratch, "Dropout", self.vars['DROPOUT_P'], 1, 1)
        self.create_box_entry(self.frame_scratch, "Max Length", self.vars['MAX_LENGTH'], 1, 2)

        self.toggle_model_ui()

    def toggle_model_ui(self):
        is_pretrained = self.vars['USE_PRETRAINED_MODEL'].get()
        if is_pretrained:
            self.set_recursive_state(self.frame_pretrained, 'normal')
            self.set_recursive_state(self.frame_scratch, 'disabled')
            self.frame_scratch.configure(text="Налаштування 'From Scratch' (Вимкнено)")
            self.frame_pretrained.configure(text="Налаштування Pretrained (Активно)")
        else:
            self.set_recursive_state(self.frame_pretrained, 'disabled')
            self.set_recursive_state(self.frame_scratch, 'normal')
            self.frame_scratch.configure(text="Налаштування 'From Scratch' (Активно)")
            self.frame_pretrained.configure(text="Налаштування Pretrained (Вимкнено)")

    def build_train_tab(self):
        self.tab_train.columnconfigure(0, weight=1)
        self.tab_train.columnconfigure(1, weight=1)

        hyper_frame = ttk.LabelFrame(self.tab_train, text="Гіперпараметри")
        hyper_frame.grid(row=0, column=0, sticky="nsew", padx=5, pady=5)
        
        self.create_grid_entry(hyper_frame, "Epochs:", self.vars['NUM_EPOCHS'], 0)
        self.create_grid_entry(hyper_frame, "Batch Size:", self.vars['BATCH_SIZE'], 1)
        self.create_grid_entry(hyper_frame, "Learning Rate:", self.vars['LEARNING_RATE'], 2)
        
        # Галочка возобновления
        ttk.Checkbutton(hyper_frame, text="Відновити з чекпоінту (--resume)", 
                        variable=self.vars['RESUME_TRAINING']).grid(row=3, column=0, columnspan=2, pady=10, padx=10, sticky="w")
        
        sys_frame = ttk.LabelFrame(self.tab_train, text="⚙️ Система и Баланс")
        sys_frame.grid(row=0, column=1, sticky="nsew", padx=5, pady=5)
        
        self.create_grid_entry(sys_frame, "Validation Split:", self.vars['VALIDATION_SPLIT'], 0)
        self.create_grid_entry(sys_frame, "Random Seed:", self.vars['SEED'], 1)
        self.create_grid_entry(sys_frame, "Max Pos Weight:", self.vars['POS_WEIGHT'], 2)

        # НОВЫЙ БЛОК: Отслеживание прогресса
        progress_frame = ttk.LabelFrame(self.tab_train, text="Прогрес навчання")
        progress_frame.grid(row=1, column=0, columnspan=2, sticky="nsew", padx=5, pady=10)
        
        self.progress_bar = ttk.Progressbar(progress_frame, orient="horizontal", mode="determinate")
        self.progress_bar.pack(fill=tk.X, padx=10, pady=10)
        
        metrics_frame = ttk.Frame(progress_frame)
        metrics_frame.pack(fill=tk.X, padx=10, pady=5)
        
        self.lbl_epoch = ttk.Label(metrics_frame, text="Епоха: 0/0", font=("Arial", 10, "bold"))
        self.lbl_epoch.pack(side=tk.LEFT, expand=True)
        
        self.lbl_train_loss = ttk.Label(metrics_frame, text="Train Loss: ---", style="Metric.TLabel")
        self.lbl_train_loss.pack(side=tk.LEFT, expand=True)
        
        self.lbl_val_loss = ttk.Label(metrics_frame, text="Val Loss: ---", style="Metric.TLabel", foreground="#d32f2f")
        self.lbl_val_loss.pack(side=tk.LEFT, expand=True)

        btn_frame = ttk.Frame(self.tab_train, padding=10)
        btn_frame.grid(row=2, column=0, columnspan=2, sticky="ew")
        
        self.btn_run = ttk.Button(btn_frame, text="▶ ЗБЕРЕГТИ НАЛАШТУВАННЯ І ЗАПУСТИТИ НАВЧАННЯ", 
                                  style="Big.TButton", command=self.start_training_process)
        self.btn_run.pack(side=tk.LEFT, fill=tk.X, expand=True, padx=(0, 5), ipady=5)
        
        self.btn_stop = ttk.Button(btn_frame, text="⏹ ЗУПИНИТИ", 
                                   style="Stop.TButton", command=self.stop_training, state=tk.DISABLED)
        self.btn_stop.pack(side=tk.RIGHT, fill=tk.X, padx=(5, 0), ipady=5)


    # ... (Вспомогательные функции create_file_entry, create_grid_entry и т.д. остаются без изменений) ...
    def create_file_entry(self, parent, label, var, row):
        frame = ttk.Frame(parent)
        frame.pack(fill=tk.X, padx=5, pady=2)
        ttk.Label(frame, text=label, width=20).pack(side=tk.LEFT)
        ttk.Entry(frame, textvariable=var).pack(side=tk.LEFT, fill=tk.X, expand=True, padx=5)
        ttk.Button(frame, text="📂", width=3, command=lambda: self.browse_file(var)).pack(side=tk.LEFT)

    def create_grid_entry(self, parent, label, var, row, width=None, col=0):
        ttk.Label(parent, text=label).grid(row=row, column=0, sticky="w", padx=10, pady=5)
        entry = ttk.Entry(parent, textvariable=var, width=width if width else 20)
        entry.grid(row=row, column=1, sticky="ew", padx=10, pady=5)

    def create_box_entry(self, parent, label, var, row, col):
        frame = ttk.Frame(parent, borderwidth=1, relief="solid", padding=5)
        frame.grid(row=row, column=col, sticky="nsew", padx=5, pady=5)
        ttk.Label(frame, text=label, font=("Arial", 8, "bold")).pack()
        ttk.Entry(frame, textvariable=var, justify="center").pack(fill=tk.X, pady=2)

    def browse_file(self, var):
        filename = filedialog.askopenfilename()
        if filename: var.set(filename)

    # ... (Методы инференса load_inference_model, run_inference, update_tags_view остаются без изменений) ...
    def load_inference_model(self):
        path = self.vars['BEST_MODEL_PATH'].get()
        if not os.path.exists(path):
            messagebox.showerror("Помилка", f"Файл не знайдено:\n{path}")
            return
        self.btn_load_model.config(state=tk.DISABLED)
        self.lbl_model_status.config(text="Завантаження...", foreground="orange")
        threading.Thread(target=self._load_model_thread, args=(path,), daemon=True).start()

    def _load_model_thread(self, path):
        try:
            self.log(f"Аналіз файлу: {path}")
            state_dict = torch.load(path, map_location='cpu')
            if 'model_state_dict' in state_dict:
                state_dict = state_dict['model_state_dict']
                
            keys = list(state_dict.keys())
            is_scratch = any("transformer_encoder" in k for k in keys)
            is_pretrained = any("transformer." in k for k in keys) and not is_scratch
            
            detected_type = "FROM_SCRATCH" if is_scratch else "PRETRAINED" if is_pretrained else "UNKNOWN"
            self.log(f"Тип архітектури: {detected_type}")

            if detected_type == "FROM_SCRATCH": self.vars['USE_PRETRAINED_MODEL'].set(False)
            elif detected_type == "PRETRAINED": self.vars['USE_PRETRAINED_MODEL'].set(True)
            self.root.after(0, self.toggle_model_ui)

            device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
            tag_map_file = self.vars['TAG_MAP_FILE'].get()
            tag_to_id, id_to_tag, num_tags, saved_threshold = data_utils.load_tag_maps(tag_map_file)
            self.vars['PREDICT_THRESHOLD'].set(saved_threshold)
            self.root.after(0, lambda: self.lbl_threshold.config(text=f"{saved_threshold:.2f}"))

            model_name = self.vars['MODEL_NAME'].get()
            if detected_type == "PRETRAINED":
                tokenizer = AutoTokenizer.from_pretrained(model_name)
                # Добавлено использование новых переменных
                model = model_pretrained.EncoderOnlyClassifier(model_name, num_tags, freeze_encoder=False, use_gradient_checkpointing=False)
            else:
                tokenizer = BertTokenizer.from_pretrained(model_name)
                model = model_scratch.EncoderOnlyClassifier(tokenizer.vocab_size, num_tags, self.vars['D_MODEL'].get(), 
                                                            self.vars['N_LAYERS'].get(), self.vars['N_HEADS'].get(), 
                                                            self.vars['FFN_HID_DIM'].get(), self.vars['DROPOUT_P'].get(), 
                                                            self.vars['MAX_LENGTH'].get())

            model.load_state_dict(state_dict, strict=True)
            model.to(device)
            model.eval()

            self.inference_model = model
            self.inference_tokenizer = tokenizer
            self.inference_id_to_tag = id_to_tag
            self.device = device
            
            self.root.after(0, lambda: self.lbl_model_status.config(text="Готова", foreground="green"))
            self.root.after(0, lambda: self.btn_predict.config(state=tk.NORMAL))
            self.root.after(0, lambda: self.btn_load_model.config(state=tk.NORMAL))
            self.log("Модель завантажена.")
        except Exception as e:
            traceback.print_exc()
            self.root.after(0, lambda: messagebox.showerror("Error", str(e)))
            self.root.after(0, lambda: self.btn_load_model.config(state=tk.NORMAL))

    def run_inference(self):
        text = self.txt_input.get("1.0", tk.END).strip()
        if not text: return
        self.btn_predict.config(state=tk.DISABLED)
        threading.Thread(target=self._predict_thread, args=(text,), daemon=True).start()

    def _predict_thread(self, text):
        try:
            encoding = self.inference_tokenizer.encode_plus(text, add_special_tokens=True, max_length=self.vars['MAX_LENGTH'].get(), 
                                                            padding='max_length', truncation=True, return_tensors='pt')
            input_ids = encoding['input_ids'].to(self.device)
            mask = encoding['attention_mask'].to(self.device)
            with torch.no_grad():
                logits = self.inference_model(input_ids, mask)
                probs = torch.sigmoid(logits).squeeze(0).cpu().numpy()
            self.inference_probs = probs
            self.root.after(0, self.update_tags_view)
            self.root.after(0, lambda: self.btn_predict.config(state=tk.NORMAL))
        except Exception as e:
            self.log(str(e))
            self.root.after(0, lambda: self.btn_predict.config(state=tk.NORMAL))

    def on_slider_move(self, val):
        self.lbl_threshold.config(text=f"{float(val):.2f}")
        if self.inference_probs is not None: self.update_tags_view()

    def update_tags_view(self):
        if self.inference_probs is None: return
        thresh = self.vars['PREDICT_THRESHOLD'].get()
        tags = [self.inference_id_to_tag[str(i)] for i, p in enumerate(self.inference_probs) if p > thresh]
        self.txt_output.delete("1.0", tk.END)
        self.txt_output.insert("1.0", ", ".join(tags) if tags else "Немає тегів")

    def load_text_file(self, ftype):
        path = filedialog.askopenfilename(filetypes=[(f"{ftype}", f"*.{ftype}")])
        if not path: return
        try:
            text = ""
            if ftype == 'txt':
                with open(path, 'r', encoding='utf-8') as f: text = f.read()
            elif ftype == 'docx' and docx:
                doc = docx.Document(path)
                text = "\n".join([p.text for p in doc.paragraphs])
            elif ftype == 'pdf' and PyPDF2:
                reader = PyPDF2.PdfReader(open(path, 'rb'))
                text = "\n".join([p.extract_text() for p in reader.pages])
            self.txt_input.delete("1.0", tk.END)
            self.txt_input.insert("1.0", text)
        except Exception as e:
            messagebox.showerror("Ошибка", str(e))

    def generate_config_content(self):
        """Обновленный метод с новыми переменными"""
        v = {k: var.get() for k, var in self.vars.items()}
        return f'''"""Generated Config"""
USE_PRETRAINED_MODEL = {v['USE_PRETRAINED_MODEL']}
DATA_FILE = r'{v['DATA_FILE']}'
TAG_MAP_FILE = r'{v['TAG_MAP_FILE']}'
BEST_MODEL_PATH = r'{v['BEST_MODEL_PATH']}'
TEXT_COLUMN_NAME = '{v['TEXT_COLUMN_NAME']}'
TAG_COLUMN_NAME = '{v['TAG_COLUMN_NAME']}'
PARSED_TAG_COLUMN = '{v['PARSED_TAG_COLUMN']}'
FILTERED_TAG_COLUMN = '{v['FILTERED_TAG_COLUMN']}'
MIN_TAG_FREQUENCY = {v['MIN_TAG_FREQUENCY']}
MODEL_NAME = '{v['MODEL_NAME']}'
MAX_LENGTH = {v['MAX_LENGTH']}
BATCH_SIZE = {v['BATCH_SIZE']}
D_MODEL = {v['D_MODEL']}
DROPOUT_P = {v['DROPOUT_P']}
N_HEADS = {v['N_HEADS']}
N_LAYERS = {v['N_LAYERS']}
FFN_HID_DIM = {v['FFN_HID_DIM']}
NUM_EPOCHS = {v['NUM_EPOCHS']}
LEARNING_RATE = {v['LEARNING_RATE']}
VALIDATION_SPLIT = {v['VALIDATION_SPLIT']}
SEED = {v['SEED']}
POS_WEIGHT = {v['POS_WEIGHT']}

# Advanced Memory & Training settings
FREEZE_ENCODER = {v['FREEZE_ENCODER']}
GRADIENT_CHECKPOINTING = {v['GRADIENT_CHECKPOINTING']}
'''

    def start_training_process(self):
        try:
            with open('config.py', 'w', encoding='utf-8') as f:
                f.write(self.generate_config_content())
            self.log("Config збережено.")
            
            # Сброс UI счетчиков
            self.progress_bar['value'] = 0
            self.progress_bar['maximum'] = self.vars['NUM_EPOCHS'].get()
            self.lbl_epoch.config(text=f"Епоха: 0/{self.vars['NUM_EPOCHS'].get()}")
            self.lbl_train_loss.config(text="Train Loss: ---")
            self.lbl_val_loss.config(text="Val Loss: ---")
            
            self.btn_run.config(state=tk.DISABLED)
            self.btn_stop.config(state=tk.NORMAL)
            
            threading.Thread(target=self.run_script, daemon=True).start()
        except Exception as e:
            messagebox.showerror("Error", str(e))

    def stop_training(self):
        """Останавливает процесс обучения"""
        if self.training_process:
            self.log("\n[!!!] Надсилання сигналу зупинки...")
            self.training_process.terminate()
            self.btn_stop.config(state=tk.DISABLED)

    def run_script(self):
        try:
            cmd = [sys.executable, "-u", "main.py"]
            if self.vars['RESUME_TRAINING'].get():
                cmd.append("--resume")
                
            self.training_process = subprocess.Popen(
                cmd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
                text=True, encoding='utf-8', errors='replace'
            )
            
            # Чтение вывода и парсинг в реальном времени
            for line in self.training_process.stdout:
                line = line.strip()
                if not line: continue
                
                self.log(line)
                self.parse_and_update_metrics(line)
                
            self.training_process.wait()
            
            if self.training_process.returncode == 0:
                self.log("✅ Навчання успішно завершено.")
            else:
                self.log("❌ Навчання перервано або завершилось з помилкою.")
                
        except Exception as e: 
            self.log(f"Помилка запуску: {str(e)}")
        finally: 
            self.training_process = None
            self.root.after(0, lambda: self.btn_run.config(state=tk.NORMAL))
            self.root.after(0, lambda: self.btn_stop.config(state=tk.DISABLED))

    def parse_and_update_metrics(self, line):
        """Парсит консольный вывод для обновления UI метрик"""
        # Ищем эпоху (напр: "--- Епоха 1 / 10 ---")
        match_epoch = re.search(r"Епоха\s+(\d+)\s+/\s+(\d+)", line)
        if match_epoch:
            curr, total = match_epoch.groups()
            self.root.after(0, lambda: self.progress_bar.config(value=int(curr)))
            self.root.after(0, lambda: self.lbl_epoch.config(text=f"Епоха: {curr}/{total}"))

        # Ищем Train Loss
        match_train = re.search(r"Середній Train Loss:\s+([0-9.]+)", line)
        if match_train:
            val = match_train.group(1)
            self.root.after(0, lambda: self.lbl_train_loss.config(text=f"Train Loss: {val}"))

        # Ищем Val Loss
        match_val = re.search(r"Середній Validation Loss:\s+([0-9.]+)", line)
        if match_val:
            val = match_val.group(1)
            self.root.after(0, lambda: self.lbl_val_loss.config(text=f"Val Loss: {val}"))

    def log(self, msg):
        self.root.after(0, lambda: (self.console_text.insert(tk.END, msg+"\n"), self.console_text.see(tk.END)))

if __name__ == "__main__":
    root = tk.Tk()
    app = ML_Launcher(root)
    root.mainloop()