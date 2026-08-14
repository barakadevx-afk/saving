module.exports = (req, res) => {
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type');

  if (req.method === 'OPTIONS') {
    return res.status(200).end();
  }

  const tiers = [
    { id: 'tier_a', name: 'Tier A - Bronze Starter', minAmount: 6000, durationDays: 3, interestRate: 0.02, description: 'Quick 3-day liquidity test' },
    { id: 'tier_b', name: 'Tier B - Silver Growth', minAmount: 10000, durationDays: 3, interestRate: 0.02, description: 'Standard 3-day capital accelerator' },
    { id: 'tier_c', name: 'Tier C - Gold Master (Popular)', minAmount: 15000, durationDays: 3, interestRate: 0.02, description: 'Most popular community choice' },
    { id: 'tier_d', name: 'Tier D - Platinum High-Yield', minAmount: 45000, durationDays: 3, interestRate: 0.02, description: 'Maximum returns for high-volume savers' }
  ];

  if (req.method === 'POST') {
    const { userId, tierId, depositAmount, currency } = req.body || {};
    const tier = tiers.find(t => t.id === tierId) || tiers[0];
    const amount = Number(depositAmount) || tier.minAmount;
    const expectedReturn = Math.round(amount * tier.interestRate);

    return res.status(200).json({
      success: true,
      message: 'Savings Cycle started successfully!',
      cycle: {
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
      }
    });
  }

  return res.status(200).json({
    success: true,
    tiers: tiers,
    adminConfig: {
      reserveFundRwf: 20000000.0,
      isPlatformLocked: false,
      lockNotice: "Platform maintenance in progress. All active deposits remain 100% safe and insured."
    }
  });
};
