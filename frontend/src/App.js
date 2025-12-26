import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

const API_BASE = process.env.REACT_APP_BACKEND_URL || '/api';

function App() {
  const [users, setUsers] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedUser, setSelectedUser] = useState(null);
  const [activeTab, setActiveTab] = useState('users');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      setLoading(true);
      console.log('Loading data from:', API_BASE);
      const [usersRes, transactionsRes] = await Promise.all([
        axios.get(`${API_BASE}/users`),
        axios.get(`${API_BASE}/transactions`)
      ]);
      setUsers(usersRes.data);
      setTransactions(transactionsRes.data);
      setError(null);
    } catch (err) {
      setError('Failed to load data. Make sure the backend server is running.');
      console.error('Error loading data:', err);
    } finally {
      setLoading(false);
    }
  };

  const getUserTransactions = (userId) => {
    return transactions.filter(t => t.userId === userId);
  };

  if (loading) {
    return (
      <div className="container">
        <div className="loading">Loading...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="container">
        <div className="error">
          <h2>Error</h2>
          <p>{error}</p>
          <button onClick={loadData} className="btn">Retry</button>
        </div>
      </div>
    );
  }

  return (
    <div className="container">
      <header className="header">
        <div className="logo">
          <svg width="40" height="40" viewBox="0 0 40 40" fill="none">
            <rect width="40" height="40" rx="8" fill="white" fillOpacity="0.2"/>
            <path d="M12 20L18 26L28 14" stroke="white" strokeWidth="3" strokeLinecap="round"/>
          </svg>
          <h1>JPMorgan Transaction Service</h1>
        </div>
        <button onClick={loadData} className="btn btn-refresh" title="Refresh Data">
          ↻ Refresh
        </button>
      </header>

      <div className="tabs">
        <button 
          className={`tab ${activeTab === 'users' ? 'active' : ''}`}
          onClick={() => { setActiveTab('users'); setSelectedUser(null); }}
        >
          Users & Balances
        </button>
        <button 
          className={`tab ${activeTab === 'transactions' ? 'active' : ''}`}
          onClick={() => { setActiveTab('transactions'); setSelectedUser(null); }}
        >
          All Transactions
        </button>
      </div>

      {activeTab === 'users' && (
        <div className="content">
          <div className="stats">
            <div className="stat-card">
              <div className="stat-value">{users.length}</div>
              <div className="stat-label">Total Users</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{transactions.length}</div>
              <div className="stat-label">Total Transactions</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">
                ${users.reduce((sum, user) => sum + parseFloat(user.balance || 0), 0).toFixed(2)}
              </div>
              <div className="stat-label">Total Balance</div>
            </div>
          </div>

          <div className="users-grid">
            {users.map(user => (
              <div 
                key={user.userId} 
                className={`user-card ${selectedUser?.userId === user.userId ? 'selected' : ''}`}
                onClick={() => setSelectedUser(selectedUser?.userId === user.userId ? null : user)}
              >
                <div className="user-avatar">
                  {user.username.charAt(0).toUpperCase()}
                </div>
                <div className="user-info">
                  <h3>{user.username}</h3>
                  <p className="user-email">{user.email}</p>
                  <div className="user-balance">
                    <span className="balance-label">Balance:</span>
                    <span className="balance-value">${parseFloat(user.balance || 0).toFixed(2)}</span>
                  </div>
                  {getUserTransactions(user.userId).length > 0 && (
                    <div className="transaction-count">
                      {getUserTransactions(user.userId).length} transaction(s)
                    </div>
                  )}
                </div>
              </div>
            ))}
          </div>

          {selectedUser && (
            <div className="user-transactions">
              <h2>Transactions for {selectedUser.username}</h2>
              {getUserTransactions(selectedUser.userId).length === 0 ? (
                <p className="no-data">No transactions found</p>
              ) : (
                <div className="transactions-list">
                  {getUserTransactions(selectedUser.userId).map(txn => (
                    <div key={txn.id} className={`transaction-item ${txn.type.toLowerCase()}`}>
                      <div className="txn-header">
                        <span className={`txn-type ${txn.type.toLowerCase()}`}>
                          {txn.type === 'CREDIT' ? '↓' : '↑'} {txn.type}
                        </span>
                        <span className="txn-status">{txn.status}</span>
                      </div>
                      <div className="txn-amount">${parseFloat(txn.amount).toFixed(2)}</div>
                      <div className="txn-description">{txn.description}</div>
                      {txn.incentiveApplied && (
                        <div className="txn-incentive">
                          🎁 Incentive: +${parseFloat(txn.incentiveAmount).toFixed(2)}
                        </div>
                      )}
                      <div className="txn-id">ID: {txn.transactionId}</div>
                      <div className="txn-time">{new Date(txn.timestamp).toLocaleString()}</div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      )}

      {activeTab === 'transactions' && (
        <div className="content">
          <h2>All Transactions</h2>
          {transactions.length === 0 ? (
            <p className="no-data">No transactions found</p>
          ) : (
            <div className="transactions-list">
              {transactions.map(txn => {
                const user = users.find(u => u.userId === txn.userId);
                return (
                  <div key={txn.id} className={`transaction-item ${txn.type.toLowerCase()}`}>
                    <div className="txn-header">
                      <span className="txn-user">{user?.username || `User #${txn.userId}`}</span>
                      <span className={`txn-type ${txn.type.toLowerCase()}`}>
                        {txn.type === 'CREDIT' ? '↓' : '↑'} {txn.type}
                      </span>
                      <span className="txn-status">{txn.status}</span>
                    </div>
                    <div className="txn-amount">${parseFloat(txn.amount).toFixed(2)}</div>
                    <div className="txn-description">{txn.description}</div>
                    {txn.incentiveApplied && (
                      <div className="txn-incentive">
                        🎁 Incentive: +${parseFloat(txn.incentiveAmount).toFixed(2)}
                      </div>
                    )}
                    <div className="txn-id">ID: {txn.transactionId}</div>
                    <div className="txn-time">{new Date(txn.timestamp).toLocaleString()}</div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}

      <footer className="footer">
        <p>JPMorgan Chase Software Engineering Job Simulation - December 2025</p>
      </footer>
    </div>
  );
}

export default App;