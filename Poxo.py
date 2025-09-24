from datetime import datetime
import random
import argparse
import sys
import os
import tempfile

try:
    import tkinter as tk
    from tkinter import ttk, messagebox, filedialog
    GUI_AVAILABLE = True
except Exception:
    GUI_AVAILABLE = False

# Lupe esta es la mini base de datos le pones que es asi por que simula cada tipo de pago
SAMPLE_DB = {
    'agua': {
        'AG1001': {'nombre': 'Pedro Enrique', 'direccion': 'Atlautla', 'monto': 420.50},
        'AG1002': {'nombre': 'Alejandro', 'direccion': 'Valle de chalco', 'monto': 3000.00},
        'AG1003': {'nombre': 'Oscar Galileo', 'direccion': 'Ozumba', 'monto': 110.99},
        'AG1004': {'nombre': 'Cristian y Brenda', 'direccion': 'Casa del chinos', 'monto': 290.50},
        'AG1005': {'nombre': 'Angel Silvon', 'direccion': 'Las vias', 'monto': 270.30},
    },
    'luz': {
        'LU1001': {'nombre': 'Pedro Enrique', 'direccion': 'Atlautla', 'monto': 120.50},
        'LU1002': {'nombre': 'Alejandro', 'direccion': 'Valle de chalco', 'monto': 10.00},
        'LU1003': {'nombre': 'Oscar Galileo', 'direccion': 'Ozumba', 'monto': 166.99},
        'LU1004': {'nombre': 'Cristian y Brenda', 'direccion': 'Casa del chinos', 'monto': 230.50},
        'LU1005': {'nombre': 'Angel Silvon', 'direccion': 'Las vias', 'monto': 70.30},
    },
    'predio': {
        'PR1001': {'nombre': 'Pedro Enrique', 'direccion': 'Atlautla', 'monto': 280.50},
        'PR1002': {'nombre': 'Alejandro', 'direccion': 'Valle de chalco', 'monto': 800.00},
        'PR1003': {'nombre': 'Oscar Galileo', 'direccion': 'Ozumba', 'monto': 900.99},
        'PR1004': {'nombre': 'Cristian y Brenda', 'direccion': 'Casa del chinos', 'monto': 230.50},
        'PR1005': {'nombre': 'Angel Silvon', 'direccion': 'Las vias', 'monto': 111.11},
    }
}

# Este codigo genera la factura si es que encuentra el codigo en la base de datos si no marca que no existe
def generate_invoice(servicio: str, codigo: str, db: dict = SAMPLE_DB, now: datetime = None) -> dict | None:
    servicio = (servicio or '').lower()
    codigo_u = (codigo or '').strip().upper()
    datos = db.get(servicio, {}).get(codigo_u)

    if not datos:
        return None

    now = now or datetime.now()
    folio = f"F-{(servicio[:2] or 'XX').upper()}-{now.strftime('%Y%m%d%H%M%S')}-{random.randint(100,999)}"
# Checa todos los datos osea los busca
    return {
        'folio': folio,
        'servicio': servicio,
        'codigo': codigo_u,
        'nombre': datos['nombre'],
        'direccion': datos.get('direccion', ''),
        'monto': datos['monto'],
        'fecha': now.strftime('%Y-%m-%d %H:%M:%S'),
        'pagado': False,
    }


def format_invoice_text(detalle: dict) -> str:
    lines = [
    # son los datos que aparecen al poner el codigo del servicio
        "RECIBO / FACTURA",
        f"Folio: {detalle.get('folio', '')}",
        f"Fecha de consulta: {detalle.get('fecha', '')}",
    ]
    if detalle.get('fecha_pago'):
        lines.append(f"Fecha pago: {detalle.get('fecha_pago')}")
    lines += [
        f"Servicio: {detalle.get('servicio', '').capitalize()}",
        f"Código: {detalle.get('codigo', '')}",
        f"Titular: {detalle.get('nombre', '')}",
        f"Dirección: {detalle.get('direccion', '')}",
        f"Monto a pagar: ${detalle.get('monto', 0):.2f}",
        f"Pagado: {'Sí' if detalle.get('pagado') else 'No'}",
        "",
        "",
    ]
    return '\n'.join(lines)


def save_receipt(detalle: dict, path: str) -> None:
    text = format_invoice_text(detalle)
    with open(path, 'w', encoding='utf-8') as f:
        f.write(text)


# ESTO ES YA EL CODIGO DE LA INTERFAZ
if GUI_AVAILABLE:
    class CajeroServiciosApp(tk.Tk):
        def __init__(self):
            super().__init__()
            self.title('Cajero de Servicios - OXXO Service')
            self.geometry('600x480')
            self.resizable(False, False)
            self.configure(bg="red")  # Fondo rojo
            self.current_invoice = None
            self._build_ui()

        def _build_ui(self):
            frame = ttk.Frame(self, padding=12)
            frame.pack(fill='both', expand=True)

            ttk.Label(frame, text='Servicio:').grid(row=0, column=0, sticky='w')
            self.servicio_var = tk.StringVar(value='Agua')
            servicio_combo = ttk.Combobox(frame, textvariable=self.servicio_var, state='readonly', width=20)
            servicio_combo['values'] = ('Agua', 'Luz', 'Predio')
            servicio_combo.grid(row=0, column=1, sticky='w')

            ttk.Label(frame, text='Código (ejemplo. AG1001):').grid(row=1, column=0, sticky='w', pady=(8, 0))
            self.codigo_entry = ttk.Entry(frame, width=25)
            self.codigo_entry.grid(row=1, column=1, sticky='w', pady=(8, 0))

            btn_frame = ttk.Frame(frame)
            btn_frame.grid(row=2, column=0, columnspan=3, pady=12, sticky='w')

            consulta_btn = ttk.Button(btn_frame, text='Consultar factura', command=self.consultar_factura)
            consulta_btn.grid(row=0, column=0, padx=4)

            pagar_btn = ttk.Button(btn_frame, text='Pagar factura', command=self.pagar_factura)
            pagar_btn.grid(row=0, column=1, padx=4)

#Este solo llama a que se guarde el codigo
            guardar_btn = ttk.Button(btn_frame, text='Guardar recibo', command=self.guardar_recibo)
            guardar_btn.grid(row=0, column=2, padx=4)

# esto limpia todos los datos de salida
            limpiar_btn = ttk.Button(btn_frame, text='Limpiar', command=self.limpiar)
            limpiar_btn.grid(row=0, column=3, padx=4)

            ttk.Label(frame, text='Factura / Recibo:').grid(row=3, column=0, sticky='nw')
            self.text_factura = tk.Text(frame, width=70, height=18, wrap='word')
            self.text_factura.grid(row=4, column=0, columnspan=3, pady=(6, 0))
            self.text_factura.config(state='disabled')

            self.estado_var = tk.StringVar(value='')
            ttk.Label(frame, textvariable=self.estado_var).grid(row=5, column=0, columnspan=3, sticky='w', pady=(6, 0))

# esto solo muestra lo que se busco previamente como el monto nombre etc
        def consultar_factura(self):
            servicio = self.servicio_var.get()
            codigo = self.codigo_entry.get().strip()
            if not codigo:
                messagebox.showinfo('Atención', 'Introduce el código del servicio.')
                return

            detalle = generate_invoice(servicio, codigo)
            if detalle is None:
                messagebox.showerror('Error', 'El código ingresado no existe. Intenta de nuevo.')
                return

            self.current_invoice = detalle
            self._mostrar_factura(detalle)
            self.estado_var.set('Factura consultada. Puedes guardar o pagar (ficticio).')

        def _mostrar_factura(self, detalle):
            self.text_factura.config(state='normal')
            self.text_factura.delete('1.0', tk.END)
            self.text_factura.insert(tk.END, format_invoice_text(detalle))
            self.text_factura.config(state='disabled')


# Esto sirve para pagar la factura
        def pagar_factura(self):
            if not self.current_invoice:
                messagebox.showwarning('Atención', 'No hay factura consultada.')
                return
            if self.current_invoice['pagado']:
                messagebox.showinfo('Información', 'La factura ya está pagada.')
                return
#Aqui solo muestra el monto y te pregunta si lo quieres pagar
            confirma = messagebox.askyesno('Confirmar pago', f"Deseas realizar el pago ficticio de ${self.current_invoice['monto']:.2f}?")
            if not confirma:
                return

            self.current_invoice['pagado'] = True
            self.current_invoice['fecha_pago'] = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
            self._mostrar_factura(self.current_invoice)
            self.estado_var.set('Pago realizado (ficticio). Puedes guardar el recibo si lo deseas.')
            messagebox.showinfo('Pago', 'Pago realizado (ficticio) correctamente.')

# Aqui es el codigo para guardar el recibo
        def guardar_recibo(self):
            if not self.current_invoice:
                messagebox.showwarning('Atención', 'No hay factura para guardar.')
                return
            default_name = f"recibo_{self.current_invoice['folio']}.txt"
            path = filedialog.asksaveasfilename(defaultextension='.txt', initialfile=default_name,
                                                filetypes=[('Text files', '*.txt'), ('All files', '*.*')])
            if not path:
                return
            try:
                save_receipt(self.current_invoice, path)
                messagebox.showinfo('Guardado', f'Recibo guardado en:\n{path}')
                self.estado_var.set(f'Recibo guardado: {path}')
            except Exception as ex:
                messagebox.showerror('Error', f'No se pudo guardar el recibo:\n{ex}')

#Aqui limpia el cuadro de textos de salida
        def limpiar(self):
            self.codigo_entry.delete(0, tk.END)
            self.text_factura.config(state='normal')
            self.text_factura.delete('1.0', tk.END)
            self.text_factura.config(state='disabled')
            self.current_invoice = None
            self.estado_var.set('Listo')


def main():
    if not GUI_AVAILABLE:
        print("Tkinter no agarro use CUI")
        return
    app = CajeroServiciosApp()
    app.mainloop()


if __name__ == '__main__':
    main()
