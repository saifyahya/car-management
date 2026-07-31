<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Valet Notification</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            background-color: #0f172a;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
            color: #e2e8f0;
            -webkit-font-smoothing: antialiased;
        }
        .container {
            max-width: 600px;
            margin: 0 auto;
            padding: 24px 16px;
        }
        .card {
            background-color: #1e293b;
            border-radius: 16px;
            border: 1px solid #334155;
            overflow: hidden;
            box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.4);
        }
        .header {
            background: linear-gradient(135deg, #0f172a 0%, #1e1b4b 50%, #312e81 100%);
            padding: 28px 24px;
            text-align: center;
            border-bottom: 1px solid #334155;
        }
        .hotel-name {
            font-size: 20px;
            font-weight: 700;
            color: #f8fafc;
            letter-spacing: 0.5px;
            margin: 0;
            text-transform: uppercase;
        }
        .content {
            padding: 32px 24px;
        }
        .message-body {
            font-size: 15px;
            color: #cbd5e1;
            line-height: 1.7;
            margin: 0;
        }
        .footer {
            padding: 20px 24px;
            text-align: center;
            border-top: 1px solid #334155;
            background-color: #0f172a;
        }
        .footer-text {
            font-size: 12px;
            color: #64748b;
            margin: 0;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="card">
            <div class="header">
                <h1 class="hotel-name">${clientName!"Hotel Valet Notification"}</h1>
            </div>
            <div class="content">
                <p class="message-body">${messageText}</p>
            </div>
            <div class="footer">
                <p class="footer-text">Hotel Valet Services &bull; Customer Notification</p>
            </div>
        </div>
    </div>
</body>
</html>
