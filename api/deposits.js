module.exports = (req, res) => {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  if (req.method === 'POST') {
    const { userId, phone, amount, momoTxId, network } = req.body || {};
    if (!amount || !momoTxId) {
      return res.status(400).json({ success: false, error: 'Amount and MoMo Transaction ID are required' });
    }

    const deposit = {
      id: 'DEP-' + Math.floor(1000 + Math.random() * 9000),
      userId: userId || 'USER-1',
      phone: phone || '+250788000000',
      amount: Number(amount),
      currency: 'RWF',
      momoTxId: momoTxId.trim(),
      network: network || 'MTN MoMo',
      status: 'APPROVED',
      createdAt: Date.now()
    };

    return res.status(200).json({
      success: true,
      message: 'Deposit confirmed! Your 3-day savings cycle is active.',
      deposit
    });
  }

  return res.status(200).json({
    success: true,
    deposits: [
      {
        id: 'DEP-8841',
        userId: 'USER-1',
        phone: '+250788123456',
        amount: 15000,
        currency: 'RWF',
        momoTxId: 'TXN89327110',
        network: 'MTN MoMo',
        status: 'APPROVED',
        createdAt: Date.now() - 86400000
      }
    ]
  });
};
