import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';

interface ChartData {
  name: string;
  income: number;
  outgoing: number;
}

interface Props {
  data: ChartData[];
}

export default function FinanceChart({ data }: Props) {
  const isDark = document.documentElement.classList.contains('dark');
  const gridStroke = isDark ? '#374151' : '#f0f0f0';
  const tickFill = isDark ? '#9ca3af' : '#6b7280';
  const tooltipStyle = isDark
    ? { borderRadius: '8px', border: '1px solid #374151', backgroundColor: '#1f2937', color: '#e5e7eb' }
    : { borderRadius: '8px', border: '1px solid #e5e7eb' };

  return (
    <ResponsiveContainer width="100%" height={350}>
      <BarChart data={data} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
        <CartesianGrid strokeDasharray="3 3" stroke={gridStroke} />
        <XAxis dataKey="name" tick={{ fontSize: 12, fill: tickFill }} />
        <YAxis tick={{ fontSize: 12, fill: tickFill }} />
        <Tooltip
          contentStyle={tooltipStyle}
          formatter={(value: number) => [`$${value.toFixed(2)}`, '']}
        />
        <Legend />
        <Bar dataKey="income" fill="#22c55e" radius={[4, 4, 0, 0]} name="Income" />
        <Bar dataKey="outgoing" fill="#ef4444" radius={[4, 4, 0, 0]} name="Outgoing" />
      </BarChart>
    </ResponsiveContainer>
  );
}
