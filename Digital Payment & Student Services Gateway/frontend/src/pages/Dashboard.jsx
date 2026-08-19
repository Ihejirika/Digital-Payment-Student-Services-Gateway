import React, { useState } from 'react';
import api from '../api/axios'; // Importing your secure interceptor

const Dashboard = () => {
    const [email, setEmail] = useState('student@kdu.edu.ng'); // Default test email
    const [amount, setAmount] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    const handlePayment = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError('');

        try {
            const payload = {
                email: email,
                amount: String(Number(amount) * 100)
            };

            const response = await api.post('/payments/initialize', payload);

            // 1. ADD THIS LINE TO SEE THE EXACT JSON STRUCTURE:
            console.log("BACKEND RESPONSE:", response.data);

            // 2. ADD SAFETY CHECKS: Try to find the URL in a few common places
            const checkoutUrl = response.data?.data?.authorization_url
                || response.data?.authorization_url;

            if (!checkoutUrl) {
                throw new Error("Checkout URL is missing from the backend response.");
            }

            window.location.href = checkoutUrl;

        } catch (err) {
            console.error(err);
            setError(err.message || 'Failed to initialize payment.');
        } finally {
            setIsLoading(false);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('jwt_token');
        window.location.href = '/login';
    };

    return (
        <div className="min-h-screen bg-gray-50 p-8">
            <div className="max-w-xl mx-auto bg-white p-8 rounded-xl shadow-lg border border-gray-200">
                <div className="flex justify-between items-center border-b pb-4 mb-6">
                    <h1 className="text-2xl font-bold text-gray-800">Student Dashboard</h1>
                    <button onClick={handleLogout} className="text-sm text-red-600 hover:text-red-800 font-medium">
                        Logout
                    </button>
                </div>

                <form onSubmit={handlePayment} className="space-y-6">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Student Email</label>
                        <input
                            type="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Fee Amount (₦)</label>
                        <input
                            type="number"
                            value={amount}
                            onChange={(e) => setAmount(e.target.value)}
                            required
                            placeholder="e.g. 50000"
                            min="100"
                            className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none"
                        />
                    </div>

                    {error && <div className="text-red-500 text-sm font-medium bg-red-50 p-3 rounded">{error}</div>}

                    <button
                        type="submit"
                        disabled={isLoading}
                        className="w-full bg-green-600 hover:bg-green-700 text-white font-bold py-3 px-4 rounded-lg transition-colors disabled:opacity-50"
                    >
                        {isLoading ? 'Connecting to Paystack...' : 'Pay Fees'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Dashboard;