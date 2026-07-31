import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, of, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class WebPushService {
  private readonly http = inject(HttpClient);

  readonly isSupported = signal<boolean>('serviceWorker' in navigator && 'PushManager' in window);
  readonly permissionGranted = signal<boolean>(
    typeof Notification !== 'undefined' && Notification.permission === 'granted'
  );
  readonly subscribing = signal<boolean>(false);

  initAndSubscribe(): void {
    if (!this.isSupported()) {
      console.warn('Web Push Notifications are not supported in this browser.');
      return;
    }

    this.subscribing.set(true);

    navigator.serviceWorker.register('/sw.js').then((reg) => {
      Notification.requestPermission().then((permission) => {
        if (permission !== 'granted') {
          this.permissionGranted.set(false);
          this.subscribing.set(false);
          return;
        }

        this.permissionGranted.set(true);

        this.http.get<{ publicKey: string }>('/api/push/public-key').pipe(
          tap((res) => {
            if (!res.publicKey) return;
            const applicationServerKey = this.urlBase64ToUint8Array(res.publicKey);

            reg.pushManager.subscribe({
              userVisibleOnly: true,
              applicationServerKey: applicationServerKey as unknown as BufferSource
            }).then((sub) => {
              this.http.post('/api/push/subscribe', sub.toJSON()).subscribe({
                next: () => {
                  console.log('Successfully subscribed valet device to Web Push Notifications');
                  this.subscribing.set(false);
                },
                error: (err) => {
                  console.error('Failed to post push subscription to backend', err);
                  this.subscribing.set(false);
                }
              });
            }).catch((err) => {
              console.error('PushManager subscribe failed', err);
              this.subscribing.set(false);
            });
          }),
          catchError((err) => {
            console.error('Failed to fetch VAPID public key', err);
            this.subscribing.set(false);
            return of(null);
          })
        ).subscribe();
      });
    }).catch((err) => {
      console.error('ServiceWorker registration failed', err);
      this.subscribing.set(false);
    });
  }

  private urlBase64ToUint8Array(base64String: string): Uint8Array {
    const padding = '='.repeat((4 - (base64String.length % 4)) % 4);
    const base64 = (base64String + padding).replace(/-/g, '+').replace(/_/g, '/');
    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; ++i) {
      outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
  }
}
