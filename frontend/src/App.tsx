import React , { ReactNode } from 'react';
import { RouterProvider, createBrowserRouter, useNavigate } from 'react-router-dom'
import axios from 'axios'
import { useEffect, useRef, useState, createContext, useContext, useCallback, FC, PropsWithChildren } from 'react'

import ReportPage from './components/ReportPage';
import Login from './components/Login';
import LoginFormData from './components/Login';

axios.defaults.withCredentials = true

const serverUrl = process.env.REACT_APP_SERVER_URL


interface AuthContextType  {
  loggedIn: boolean;
  user: string | null;
  cookieHeaders: string[]| null;

  checkLoginState: () => Promise<void>;
  logout?: () => void;

};
const AuthContext = createContext<AuthContextType>({} as AuthContextType);

/*interface PropsWithChildren {
  children?: ReactNode; // Explicitly define children as optional ReactNode
}*/

export const AuthContextProvider:FC<PropsWithChildren> = ({ children }) => {
  const [user, setUser] = useState<string | null>(null)
  const [cookieHeaders, setCookieHeaders] = useState<string[] | null>(null)
  const [loggedIn, setLoggedIn] = useState(user !== null)
  //const [logout, setlogout] = useState(user != null)

  const checkLoginState = useCallback(async () => {
    try {
        var credentials = btoa("admin1" + ':' + "admin123");

        const r = await axios({method: "post",
          url : `${serverUrl}/auth`,
          headers: {
            'Authorization': `Basic ${credentials}`,
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': 'http://localhost:8084/auth',
            'Access-Control-Allow-Headers': 'Content-Type, Authorization, Custom-Header',
            'Access-Control-Allow-Credentials': 'true',
            'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS'
            }}
          ).then(response => {
            let cookieArray: string[] = [];
            if (typeof response.headers.getSetCookie === 'function') {
                // If the function exists, call it and assign the result
                cookieArray = response.headers.getSetCookie();
            }
            setCookieHeaders(cookieArray)
            user && setUser(user)
              console.log('Response:', response.data);
            })

    } catch (err) {
      console.error(err)
    }
  }, [])

  useEffect(() => {
    checkLoginState()
  }, [checkLoginState])

  const login = async (userData: string) => {
    setUser(userData);
  };



  const logout = () => {
    setUser(null);
    setCookieHeaders(null);
    localStorage.removeItem("site");
    setLoggedIn(false)
    //navigate("/login");
  };

  return (
    <AuthContext.Provider value ={{loggedIn, cookieHeaders, checkLoginState, user, logout}}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  console.log(context) 
  if (context === null) {
    throw new Error('useAuth must be used within an AuthContextProvider');
  }
  return context;
};

const App: React.FC = () => {
  const { loggedIn, user, logout } = useAuth();
  return (

      <div className="App">
        {loggedIn ? <ReportPage />:<Login />}
      </div>

  );
};



export default App;