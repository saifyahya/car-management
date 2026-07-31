// Service Worker for Valet App Web Push Notifications

self.addEventListener('push', (event) => {
  let data = { title: '🚗 Vehicle Requested!', body: 'A vehicle retrieval request has been submitted.' };
  if (event.data) {
    try {
      data = event.data.json();
    } catch (e) {
      data.body = event.data.text();
    }
  }

  const options = {
    body: data.body,
    icon: '/logo.jpg',
    badge: '/logo.jpg',
    vibrate: [200, 100, 200, 100, 200, 100, 400],
    data: {
      url: data.ticketId ? `/tickets/${data.ticketId}` : '/dashboard'
    },
    actions: [
      { action: 'open', title: 'Open Ticket' }
    ]
  };

  event.waitUntil(
    self.registration.showNotification(data.title || '🚗 Vehicle Requested!', options)
  );
});

self.addEventListener('notificationclick', (event) => {
  event.notification.close();
  const targetUrl = (event.notification.data && event.notification.data.url) ? event.notification.data.url : '/dashboard';

  event.waitUntil(
    clients.matchAll({ type: 'window', includeUncontrolled: true }).then((windowClients) => {
      for (let client of windowClients) {
        if (client.url.includes(targetUrl) && 'focus' in client) {
          return client.focus();
        }
      }
      if (clients.openWindow) {
        return clients.openWindow(targetUrl);
      }
    })
  );
});
