<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Valet Ticket Confirmation</title>
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
            padding: 32px 24px;
            text-align: center;
            border-bottom: 1px solid #334155;
        }
        .logo-icon {
            font-size: 36px;
            margin-bottom: 8px;
            display: inline-block;
        }
        .hotel-name {
            font-size: 20px;
            font-weight: 700;
            color: #f8fafc;
            letter-spacing: 0.5px;
            margin: 0;
            text-transform: uppercase;
        }
        .header-subtitle {
            font-size: 13px;
            color: #94a3b8;
            margin-top: 4px;
            text-transform: uppercase;
            letter-spacing: 1.5px;
        }
        .content {
            padding: 32px 24px;
        }
        .greeting {
            font-size: 18px;
            font-weight: 600;
            color: #ffffff;
            margin-top: 0;
            margin-bottom: 12px;
        }
        .lead-text {
            font-size: 14px;
            color: #94a3b8;
            line-height: 1.6;
            margin-bottom: 24px;
        }
        .pin-box {
            background: linear-gradient(135deg, #1e1b4b 0%, #312e81 100%);
            border: 2px dashed #6366f1;
            border-radius: 12px;
            padding: 20px;
            text-align: center;
            margin-bottom: 28px;
        }
        .pin-label {
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: 1.5px;
            color: #a5b4fc;
            font-weight: 600;
            margin-bottom: 6px;
        }
        .pin-code {
            font-size: 34px;
            font-weight: 800;
            letter-spacing: 6px;
            color: #ffffff;
            font-family: 'Courier New', Courier, monospace;
            margin: 4px 0;
            text-shadow: 0 2px 4px rgba(0,0,0,0.3);
        }
        .pin-subtext {
            font-size: 12px;
            color: #cbd5e1;
            margin-top: 6px;
        }
        .details-grid {
            background-color: #0f172a;
            border-radius: 12px;
            padding: 16px 20px;
            margin-bottom: 28px;
            border: 1px solid #334155;
        }
        .details-row {
            display: flex;
            justify-content: space-between;
            padding: 8px 0;
            border-bottom: 1px solid #1e293b;
        }
        .details-row:last-child {
            border-bottom: none;
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
        .cta-container {
            text-align: center;
            margin-top: 10px;
            margin-bottom: 10px;
        }
        .btn-request {
            display: inline-block;
            background: linear-gradient(135deg, #4f46e5 0%, #6366f1 100%);
            color: #ffffff !important;
            text-decoration: none;
            font-weight: 700;
            font-size: 15px;
            padding: 14px 32px;
            border-radius: 10px;
            box-shadow: 0 4px 14px rgba(99, 102, 241, 0.4);
            letter-spacing: 0.5px;
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
                <div class="logo-icon">🔑</div>
                <h1 class="hotel-name">${clientName!"Hotel Valet Services"}</h1>
                <div class="header-subtitle">Digital Valet Ticket #${ticketNumber}</div>
            </div>

            <div class="content">
                <h2 class="greeting">Welcome! Your vehicle is securely parked.</h2>
                <p class="lead-text">
                    Thank you for using our valet service. Your vehicle has been checked in and is parked safely.
                    When you are ready to depart, tap the button below to request your vehicle.
                </p>

                <#if requestUrl?? && requestUrl?has_content>
                <div class="cta-container" style="margin-bottom: 28px;">
                    <a href="${requestUrl}" class="btn-request" target="_blank">Request My Vehicle</a>
                </div>
                </#if>

                <div class="pin-box">
                    <div class="pin-label">Your Pickup Security PIN</div>
                    <div class="pin-code">${pickupPin}</div>
                    <div class="pin-subtext">Show this PIN to the valet attendant upon vehicle retrieval</div>
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
                        <#if checkedInTime?? && checkedInTime?has_content>
                        <tr>
                            <td class="detail-label" style="padding: 6px 0; border-top: 1px solid #1e293b;">Checked In</td>
                            <td class="detail-value" style="padding: 6px 0; text-align: right; border-top: 1px solid #1e293b;">${checkedInTime}</td>
                        </tr>
                        </#if>
                    </table>
                </div>
            </div>

            <div class="footer">
                <p class="footer-text">
                    This is an automated valet notification from ${clientName!"Hotel Valet"}.<br>
                    Please do not reply directly to this email.
                </p>
            </div>
        </div>
    </div>
</body>
</html>
