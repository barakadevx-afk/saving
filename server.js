const express = require('express');
const cors = require('cors');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));
app.use(express.static(__dirname));

// In-Memory Database Store (Syncs across endpoints)
let db = {
  adminConfig: {
    reserveFundRwf: 20000000.0,
    isPlatformLocked: false,
    lockNotice: "Platform maintenance in progress. All active deposits remain 100% safe and insured."
  },
  tiers: [
    { id: 'tier_a', name: 'Tier A - Bronze Starter', minAmount: 6000, durationDays: 3, interestRate: 0.02, description: 'Quick 3-day liquidity test' },
    { id: 'tier_b', name: 'Tier B - Silver Growth', minAmount: 10000, durationDays: 3, interestRate: 0.02, description: 'Standard 3-day capital accelerator' },
    { id: 'tier_c', name: 'Tier C - Gold Master (Popular)', minAmount: 15000, durationDays: 3, interestRate: 0.02, description: 'Most popular community choice' },
    { id: 'tier_d', name: 'Tier D - Platinum High-Yield', minAmount: 45000, durationDays: 3, interestRate: 0.02, description: 'Maximum returns for high-volume savers' }
  ],
  cycles: [
    {
      id: 'CYC-1001',
      userId: 'USER-1',
      tierId: 'tier_c',
      tierName: 'Tier C - Gold Master',
      depositAmount: 15000,
      interestRate: 0.02,
      expectedReturnAmount: 300,
      totalPayoutAmount: 15300,
      startedAt: Date.now() - (1.5 * 86400000), // 1.5 days ago
      maturityDate: Date.now() + (1.5 * 86400000),
      durationDays: 3,
      status: 'ACTIVE', // ACTIVE, MATURED, WITHDRAWN
      currency: 'RWF'
    }
  ],
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
  ],
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
  ],
  announcements: [
    {
      id: 'ANN-1',
      title: '🛡️ 20,000,000 RWF Capital Reserve Guaranteed',
      content: 'All 3-day savings cycles are 100% insured under SMART FUTURE CAPITAL (SFC) liquidity reserves.',
      category: 'SECURITY',
      isUrgent: false,
      createdAt: Date.now() - (2 * 86400000)
    },
    {
      id: 'ANN-2',
      title: '⚡ Instant MoMo & Airtel Money Merchant Deposition',
      content: 'Deposits via merchant code *182*8*1*1799283# are now verified and activated within minutes.',
      category: 'UPDATE',
      isUrgent: false,
      createdAt: Date.now() - 86400000
    }
  ]
};

// API: Health
app.get('/api/health', (req, res) => {
  res.json({ status: 'ok', time: new Date().toISOString(), platform: 'SMART FUTURE CAPITAL (SFC) React JSX Backend' });
});

// API: Savings Cycles & Tiers
app.get('/api/cycles', (req, res) => {
  res.json({
    success: true,
    tiers: db.tiers,
    activeCycles: db.cycles,
    adminConfig: db.adminConfig
  });
});

app.post('/api/cycles', (req, res) => {
  const { userId, tierId, depositAmount, currency } = req.body;
  const tier = db.tiers.find(t => t.id === tierId) || db.tiers[0];
  const amount = Number(depositAmount) || tier.minAmount;
  const expectedReturn = Math.round(amount * tier.interestRate);

  const newCycle = {
    id: 'CYC-' + Math.floor(1000 + Math.random() * 9000),
    userId: userId || 'USER-1',
    tierId: tier.id,
    tierName: tier.name,
    depositAmount: amount,
    interestRate: tier.interestRate,
    expectedReturnAmount: expectedReturn,
    totalPayoutAmount: amount + expectedReturn,
    startedAt: Date.now(),
    maturityDate: Date.now() + (tier.durationDays * 86400000),
    durationDays: tier.durationDays,
    status: 'ACTIVE',
    currency: currency || 'RWF'
  };

  db.cycles.unshift(newCycle);
  res.json({ success: true, message: 'Savings Cycle started successfully!', cycle: newCycle });
});

// API: Deposits
app.get('/api/deposits', (req, res) => {
  res.json({ success: true, deposits: db.deposits });
});

app.post('/api/deposits', (req, res) => {
  const { userId, phone, amount, momoTxId, network, tierId } = req.body;
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

  db.deposits.unshift(deposit);

  // Auto-activate cycle
  if (tierId) {
    const tier = db.tiers.find(t => t.id === tierId) || db.tiers[0];
    const expectedReturn = Math.round(deposit.amount * tier.interestRate);
    const newCycle = {
      id: 'CYC-' + Math.floor(1000 + Math.random() * 9000),
      userId: deposit.userId,
      tierId: tier.id,
      tierName: tier.name,
      depositAmount: deposit.amount,
      interestRate: tier.interestRate,
      expectedReturnAmount: expectedReturn,
      totalPayoutAmount: deposit.amount + expectedReturn,
      startedAt: Date.now(),
      maturityDate: Date.now() + (tier.durationDays * 86400000),
      durationDays: tier.durationDays,
      status: 'ACTIVE',
      currency: 'RWF'
    };
    db.cycles.unshift(newCycle);
  }

  res.json({ success: true, message: 'Deposit recorded and cycle activated!', deposit });
});

// API: Withdrawals
app.get('/api/withdrawals', (req, res) => {
  res.json({ success: true, withdrawals: db.withdrawals });
});

app.post('/api/withdrawals', (req, res) => {
  const { userId, phone, amount } = req.body;
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

  db.withdrawals.unshift(withdrawal);
  res.json({ success: true, message: 'Withdrawal request submitted! Payout processed via MoMo.', withdrawal });
});

// API: Admin Operations
app.get('/api/admin', (req, res) => {
  res.json({
    success: true,
    adminConfig: db.adminConfig,
    deposits: db.deposits,
    withdrawals: db.withdrawals,
    cycles: db.cycles,
    announcements: db.announcements
  });
});

app.post('/api/admin/reserve', (req, res) => {
  const { amount } = req.body;
  if (amount) {
    db.adminConfig.reserveFundRwf = Number(amount);
  }
  res.json({ success: true, reserveFundRwf: db.adminConfig.reserveFundRwf });
});

app.post('/api/admin/announcements', (req, res) => {
  const { title, content, category, isUrgent } = req.body;
  const ann = {
    id: 'ANN-' + Math.floor(1000 + Math.random() * 9000),
    title: title || 'System Notification',
    content: content || '',
    category: category || 'ANNOUNCEMENT',
    isUrgent: Boolean(isUrgent),
    createdAt: Date.now()
  };
  db.announcements.unshift(ann);
  res.json({ success: true, announcement: ann });
});

app.delete('/api/admin/announcements/:id', (req, res) => {
  const { id } = req.params;
  db.announcements = db.announcements.filter(a => a.id !== id);
  res.json({ success: true, message: 'Announcement deleted' });
});

// Catch-all route for SPA React frontend
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

if (process.env.NODE_ENV !== 'production' || !process.env.VERCEL) {
  app.listen(PORT, '0.0.0.0', () => {
    console.log(`[SFC Backend & React Server] Listening on http://0.0.0.0:${PORT}`);
  });
}

module.exports = app;
