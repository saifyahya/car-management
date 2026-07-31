import { Injectable, signal } from '@angular/core';

export interface ToastNotification {
  id: number;
  message: string;
  type: 'success' | 'error' | 'info';
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  readonly toasts = signal<ToastNotification[]>([]);
  private counter = 0;

  showSuccess(message: string) {
    this.addToast(message, 'success');
  }

  showError(message: string) {
    this.addToast(message, 'error');
  }

  private addToast(message: string, type: 'success' | 'error' | 'info') {
    const id = ++this.counter;
    const toast: ToastNotification = { id, message, type };
    this.toasts.update(list => [...list, toast]);

    setTimeout(() => {
      this.remove(id);
    }, 4500);
  }

  remove(id: number) {
    this.toasts.update(list => list.filter(t => t.id !== id));
  }
}
