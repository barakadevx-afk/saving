module.exports = (req, res) => {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  if (req.method === 'POST') {
    const { userId, phone, amount } = req.body || {};
    if (!phone || !amount) {
      return res.status(400).json({ success: false, error: 'Phone number and withdrawal amount are required' });
    }

    const withdrawal = {
      id: 'WTH-' + Math.floor(1000 + Math.random() * 9000),
      userId: userId || 'USER-1',
      phone: phone.trim(),
      amount: Number(amount),
      currency: 'RWF',
      status: 'PENDING',
      createdAt: Date.now()
    };

    return res.status(200).json({
      success: true,
      message: 'Withdrawal request submitted! Payout processed via MoMo.',
      withdrawal
    });
  }

  return res.status(200).json({
    success: true,
    withdrawals: [
      {
        id: 'WTH-3021',
        userId: 'USER-1',
        phone: '+250788123456',
        amount: 15300,
        currency: 'RWF',
        status: 'PENDING',
        createdAt: Date.now() - (4 * 3600000)
      }
    ]
  });
};
