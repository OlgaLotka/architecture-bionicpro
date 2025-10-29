import React , { ReactNode } from 'react';
import { RouterProvider, createBrowserRouter, useNavigate } from 'react-router-dom'
import axios from 'axios'
import { useEffect, useRef, useState, createContext, useContext, useCallback, FC, PropsWithChildren } from 'react'

import ReportPage from './components/ReportPage';
import Login from './components/Login';

axios.defaults.withCredentials = true

const serverUrl = process.env.REACT_APP_SERVER_URL

type AuthContextType = {
  loggedIn: boolean;
  user: string | null;
  checkLoginState: () => Promise<void>;
  logout?: () => void;

};
const AuthContext = createContext<AuthContextType | null>(null);

/*interface PropsWithChildren {
  children?: ReactNode; // Explicitly define children as optional ReactNode
}*/

export const AuthContextProvider:FC<PropsWithChildren> = ({ children }) => {
  const [user, setUser] = useState(null)
  const [loggedIn, setLoggedIn] = useState(user !== null)
  //const [logout, setlogout] = useState(user != null)
  const checkLoginState = useCallback(async () => {
    try {
      const {
        data: { loggedIn: logged_in, user },
      } = await axios.get(`${serverUrl}/auth`)
      setLoggedIn(logged_in)
      user && setUser(user)
    } catch (err) {
      console.error(err)
    }
  }, [])

  useEffect(() => {
    checkLoginState()
  }, [checkLoginState])

  const logout = () => {
    setUser(null);
    //setToken("");
    localStorage.removeItem("site");
    setLoggedIn(false)
    //navigate("/login");
  };

  return (
    <AuthContext.Provider value ={{loggedIn, checkLoginState, user, logout}}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === null) {
    throw new Error('useAuth must be used within an AuthContextProvider');
  }
  return context;
};

const App: React.FC = () => {
    const { loggedIn, user, logout } = useAuth();
  return (

      <div className="App">
            <header>
              <h1>My App</h1>
              {loggedIn ? (
                <>
                  <span>Welcome, {user}!</span>
                  <button onClick={logout}>Logout</button>
                </>
              ) : (
                  <button onClick={() => loggedIn}>Login</button>

              )}
            </header>

        {loggedIn ? <ReportPage />:<Login />}
      </div>

  );
};

export default App;