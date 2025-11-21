import React, { useState } from 'react';
import { useAuth } from '../App';
import Login from './Login'

const ReportPage: React.FC = () => {

  const { loggedIn: initialLoginStatus, logout, cookieHeaders } = useAuth();
  const [loading, setLoading] = useState(false);
  const [datas, setDatas] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loggedIn2, setLoggedIn] = useState(initialLoginStatus);

  const downloadReport = async () => {
    if (!loggedIn2) {
      setError('Not authenticated');
      return;
    }

    try {
      setLoading(true);
      setError(null);

      const response = await fetch(`${process.env.REACT_APP_API_URL||'http://localhost:8085'}/reports`, {
        method: 'GET',
        headers: {
           'Content-Type': 'application/json'
        },
         credentials: 'include'
      });
      if (response.status == 401){
        logout2();
      }
      const resultText: string = await response.text();
     setDatas(resultText)
    } catch (err) {
      setDatas(null);
      setError(err instanceof Error ? err.message : 'An error occurred');
    } finally {
      setLoading(false);
    }
  };

  if (!loggedIn2 && loading) {
    return <div>Loading...</div>;
  }

  if (!loggedIn2) {
    return (
     <Login />
    );
  }

    const logout2 = async () => {
      logout();
      setLoggedIn(false);
      setLoading(false);

    }

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
      <div className="p-8 bg-white rounded-lg shadow-md">
        <h1 className="text-2xl font-bold mb-6">Usage Reports</h1>
        <div>{datas}</div>
        <button
          onClick={downloadReport}
          disabled={loading}
          className={`px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 ${
            loading ? 'opacity-50 cursor-not-allowed' : ''
          }`}
        >
          {loading ? 'Generating Report...' : 'Download Report'}
        </button>
                <button
          onClick={logout2}
          disabled={loading}
          className={`px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 ${
            loading ? 'opacity-50 cursor-not-allowed' : ''
          }`}
        >
          logout
        </button>

        {error && (
          <div className="mt-4 p-4 bg-red-100 text-red-700 rounded">
            {error}
          </div>
        )}
      </div>
    </div>
  );
};

export default ReportPage;