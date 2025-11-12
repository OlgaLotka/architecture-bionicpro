import {Form, Input, Button} from 'antd';
import {  UserOutlined, LockOutlined  }  from  '@ant-design/icons';
import axios from 'axios'
import React, {FormEvent, useState, useCallback, useContext } from 'react';
import './Login.css'
import { useAuth } from '../App';

const serverUrl = process.env.REACT_APP_SERVER_URL


interface LoginFormData {
  user: string;
  password: string;
}

  /*function handleClick(event:FormEvent) {
        event.preventDefault();
        const [username, setUsername] = useState('');
      const [password, setPassword] = useState('');
       
  }*/

const Login = () =>  {

 
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
    const handleSubmit = async () => {

     
    const context = useAuth();
    const [user, setUser] = useState('');
    const [password, setPassword] = useState('');
    //context.user = values.user;
    //context.password = values.password;
     // const [username, setUsername] = useState('');
     //const [password, setPassword] = useState('');
    
      var credentials = btoa(username + ':' + password);
      try {
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
            setUser(user);
            setPassword(password);
           /* AuthContextProvider.arguments.
            const {user, login } = useContext(AuthContext);
  
            const [token, acc_token] = useState(JSON.stringify(response.data.token));
            const [loggedIn, setLoggedIn] = useState(user !== null)
            login(values.user);
            acc_token(token);*/
              console.log('Response:', response.data);
            })
      } catch (err) {
        console.error(err)
      }
    };
  
  
  return (
    <header className="Login-header">
      
    <form onSubmit={handleSubmit}>
      <input type="text" placeholder="Username" value={username} onChange={(e) => setUsername(e.target.value)} />
      <input type="password" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} />
      <button type="submit">Log In</button>
    </form>
    </header> 
  );

} 


export default Login;