import { useEffect, useState } from 'react';
import { api } from '../api/client';
import FinanceChart from '../components/FinanceChart';
import { usePolling } from '../hooks/usePolling';

interface Income {
  id: number;
  title: string;
  value: number;
  status: string;
  categoryIncome: { id: number; title: string };
}

interface Outgoing {
  id: number;
  title: string;
  value: number;
  status: string;
  categoryOutgoing: { id: number; title: string };
}

export default function Dashboard() {
  const [incomes, setIncomes] = useState<Income[]>([]);
  const [outgoings, setOutgoings] = useState<Outgoing[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      api.get<Income[]>('/incomes'),
      api.get<Outgoing[]>('/outgoings'),
    ]).then(([inc, out]) => {
      setIncomes(inc);
      setOutgoings(out);
      setLoading(false);
    });
  }, []);

  const fetchData = async () => {
    const [inc, out] = await Promise.all([
      api.get<Income[]>('/incomes'),
      api.get<Outgoing[]>('/outgoings'),
    ]);
    setIncomes(inc);
    setOutgoings(out);
    setLoading(false);
  };

  usePolling(fetchData, 30000);

  const totalIncome = incomes.reduce((sum, i) => sum + i.value, 0);
  const totalOutgoing = outgoings.reduce((sum, o) => sum + o.value, 0);
  const balance = totalIncome - totalOutgoing;

  const categoryMap = new Map<string, { income: number; outgoing: number }>();
  incomes.forEach((i) => {
    const cat = i.categoryIncome.title;
    const entry = categoryMap.get(cat) || { income: 0, outgoing: 0 };
    entry.income += i.value;
    categoryMap.set(cat, entry);
  });
  outgoings.forEach((o) => {
    const cat = o.categoryOutgoing.title;
    const entry = categoryMap.get(cat) || { income: 0, outgoing: 0 };
    entry.outgoing += o.value;
    categoryMap.set(cat, entry);
  });
  const chartData = Array.from(categoryMap.entries()).map(([name, data]) => ({
    name,
    income: data.income,
    outgoing: data.outgoing,
  }));

  if (loading) {
    return <div className="flex items-center justify-center h-64 text-gray-500 dark:text-gray-400">Loading...</div>;
  }

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-800 dark:text-gray-100 mb-6">Dashboard</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white dark:bg-gray-900 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
          <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Total Income</p>
          <p className="text-3xl font-bold text-green-600">${totalIncome.toFixed(2)}</p>
          <p className="text-sm text-gray-400 dark:text-gray-500 mt-2">{incomes.length} records</p>
        </div>
        <div className="bg-white dark:bg-gray-900 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
          <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Total Outgoing</p>
          <p className="text-3xl font-bold text-red-500">${totalOutgoing.toFixed(2)}</p>
          <p className="text-sm text-gray-400 dark:text-gray-500 mt-2">{outgoings.length} records</p>
        </div>
        <div className="bg-white dark:bg-gray-900 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
          <p className="text-sm text-gray-500 dark:text-gray-400 mb-1">Balance</p>
          <p className={`text-3xl font-bold ${balance >= 0 ? 'text-blue-600' : 'text-red-600'}`}>
            ${balance.toFixed(2)}
          </p>
          <p className="text-sm text-gray-400 dark:text-gray-500 mt-2">{balance >= 0 ? 'Positive' : 'Negative'}</p>
        </div>
      </div>

      <div className="bg-white dark:bg-gray-900 rounded-xl shadow-sm border border-gray-200 dark:border-gray-700 p-6">
        <h2 className="text-lg font-semibold text-gray-700 dark:text-gray-200 mb-4">Income vs Outgoings by Category</h2>
        <FinanceChart data={chartData} />
      </div>
    </div>
  );
}
