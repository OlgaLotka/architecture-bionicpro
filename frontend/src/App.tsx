//import React from 'react';
import { RouterProvider, createBrowserRouter, useNavigate } from 'react-router-dom'
import axios from 'axios'
import { useEffect, useRef, useState, createContext, useContext, useCallback } from 'react'

import ReportPage from './components/ReportPage';

axios.defaults.withCredentials = true

const serverUrl = "http://localhost:8084//process.env.REACT_APP_SERVER_URL

const AuthContext = createContext();

const AuthContextProvider = ({ children }) => {
  const [loggedIn, setLoggedIn] = useState(null)
  const [user, setUser] = useState(null)

const checkLoginState = useCallback(async () => {
    try {
      const {
        data: { loggedIn: logged_in, user },
      } = await axios.get(`${serverUrl}/auth/logged_in`)
      setLoggedIn(logged_in)
      user && setUser(user)
    } catch (err) {
      console.error(err)
    }
}, [])

useEffect(() => {
    checkLoginState()
}, [checkLoginState])

  return (
    <AuthContext.Provider value={{ loggedIn, checkLoginState, user }}>
      {children}
    </AuthContext.Provider>
  )
}
const App: React.FC = () => {
  return (
    <AuthContext.Provider value={{ loggedIn, checkLoginState, user }}>
      <div className="App">
        <ReportPage />
      </div>
    </AuthContext.Provider>
  );
};

export default App;