<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Vehicle Ready for Pickup</title>
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
            background: linear-gradient(135deg, #064e3b 0%, #047857 100%);
            padding: 32px 24px;
            text-align: center;
            border-bottom: 1px solid #059669;
        }
        .logo-icon {
            font-size: 40px;
            margin-bottom: 8px;
            display: inline-block;
        }
        .title {
            font-size: 22px;
            font-weight: 800;
            color: #ffffff;
            letter-spacing: 0.5px;
            margin: 0;
            text-transform: uppercase;
        }
        .header-subtitle {
            font-size: 13px;
            color: #a7f3d0;
            margin-top: 6px;
            font-weight: 600;
            letter-spacing: 1px;
        }
        .content {
            padding: 32px 24px;
        }
        .lead-text {
            font-size: 15px;
            color: #cbd5e1;
            line-height: 1.6;
            margin-bottom: 24px;
            text-align: center;
        }
        .pin-box {
            background: linear-gradient(135deg, #064e3b 0%, #065f46 100%);
            border: 2px dashed #10b981;
            border-radius: 12px;
            padding: 20px;
            text-align: center;
            margin-bottom: 28px;
        }
        .pin-label {
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: 1.5px;
            color: #6ee7b7;
            font-weight: 600;
            margin-bottom: 6px;
        }
        .pin-code {
            font-size: 36px;
            font-weight: 800;
            letter-spacing: 6px;
            color: #ffffff;
            font-family: 'Courier New', Courier, monospace;
            margin: 4px 0;
            text-shadow: 0 2px 4px rgba(0,0,0,0.3);
        }
        .pin-subtext {
            font-size: 12px;
            color: #a7f3d0;
            margin-top: 6px;
        }
        .details-grid {
            background-color: #0f172a;
            border-radius: 12px;
            padding: 16px 20px;
            margin-bottom: 24px;
            border: 1px solid #334155;
        }
        .detail-label {
            color: #64748b;
            font-size: 13px;
            font-weight: 500;
        }
        .detail-value {
            color: #f1f5f9;
            font-size: 13px;
            font-weight: 600;
            text-align: right;
        }
        .footer {
            padding: 24px;
            text-align: center;
            border-top: 1px solid #334155;
            background-color: #0f172a;
        }
        .footer-text {
            font-size: 12px;
            color: #64748b;
            line-height: 1.5;
            margin: 0;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="card">
            <div class="header">
                <div class="logo-icon">🚗✨</div>
                <h1 class="title">Your Vehicle is Ready!</h1>
                <div class="header-subtitle">${clientName!"Hotel Valet"} &bull; Ticket #${ticketNumber}</div>
            </div>

            <div class="content">
                <p class="lead-text">
                    Your vehicle has been retrieved by our valet team and is waiting for you at the main pickup counter.
                </p>

                <div class="pin-box">
                    <div class="pin-label">Show PIN to Valet Attendant</div>
                    <div class="pin-code">${pickupPin}</div>
                    <div class="pin-subtext">Present this PIN to collect your keys</div>
                </div>

                <div class="details-grid">
                    <table width="100%" cellpadding="0" cellspacing="0" style="border-collapse: collapse;">
                        <tr>
                            <td class="detail-label" style="padding: 6px 0;">Ticket Number</td>
                            <td class="detail-value" style="padding: 6px 0; text-align: right;"><strong>${ticketNumber}</strong></td>
                        </tr>
                        <#if vehicleInfo?? && vehicleInfo?has_content>
                        <tr>
                            <td class="detail-label" style="padding: 6px 0; border-top: 1px solid #1e293b;">Vehicle</td>
                            <td class="detail-value" style="padding: 6px 0; text-align: right; border-top: 1px solid #1e293b;">${vehicleInfo}</td>
                        </tr>
                        </#if>
                        <#if plateNumber?? && plateNumber?has_content>
                        <tr>
                            <td class="detail-label" style="padding: 6px 0; border-top: 1px solid #1e293b;">License Plate</td>
                            <td class="detail-value" style="padding: 6px 0; text-align: right; border-top: 1px solid #1e293b;"><strong>${plateNumber}</strong></td>
                        </tr>
                        </#if>
                    </table>
                </div>
            </div>

            <div class="footer">
                <p class="footer-text">
                    Thank you for staying with us at ${clientName!"Hotel Valet"}. Have a safe drive!
                </p>
            </div>
        </div>
    </div>
</body>
</html>
