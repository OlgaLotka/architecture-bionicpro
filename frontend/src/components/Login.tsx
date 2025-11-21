import {Form, Input, Button} from 'antd';
import {  UserOutlined, LockOutlined  }  from  '@ant-design/icons';
import axios from 'axios'
import React, {FormEvent, useState, useCallback, useContext } from 'react';
import './Login.css'
import { useAuth } from '../App';
import { generateCodeVerifier} from './pkceUtils';
const serverUrl = process.env.REACT_APP_SERVER_URL || 'http://localhost:8084'

axios.defaults.withCredentials = true


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

  const { loggedIn, user, login} = useAuth();
  //const [username, setUsername] = useState('');
  //const [password, setPassword] = useState('');
    const onFinish = async (values: LoginFormData) => {
      const codeVerifier = generateCodeVerifier(128);
      localStorage.setItem('pkce_code_verifier', codeVerifier); 
    /**const context = useAuth();
    const [user, setUser] = useState('');
    const [password, setPassword] = useState('');*/
    //context.user = values.user;
    //context.password = values.password;
     // const [username, setUsername] = useState('');
     //const [password, setPassword] = useState('');
      //var credentials = btoa("admin1" + ':' + "admin123");
      var credentials = btoa(values.user + ':' + values.password);
      try {
        const r = await fetch(`${serverUrl}/auth`,
          {method: "post",
          headers: {     
            'Authorization': `Basic ${credentials}`,       
            'Content-Type': 'application/x-www-form-urlencoded',
            //'Set-Cookie': `code_verifier=${codeVerifier};Secure; HttpOnly`,
            //'Access-Control-Allow-Origin': 'http://localhost:3000',
            //'Access-Control-Allow-Headers': 'Content-Type, Authorization, Custom-Header',
           // 'Access-Control-Allow-Credentials': 'true',
            //'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS'
            },
          credentials: 'include'}
          ).then(response => {
           /* setUser(user);
            setPassword(password);*/
           /* AuthContextProvider.arguments.
            const {user, login } = useContext(AuthContext);
  
            const [token, acc_token] = useState(JSON.stringify(response.data.token));
            const [loggedIn, setLoggedIn] = useState(user !== null)
            login(values.user);
            acc_token(token);*/
            let cookieArray: string[] = [];
            /*if (typeof response.headers.getSetCookie === 'function') {
                // If the function exists, call it and assign the result
                cookieArray = response.headers.getSetCookie();
            }
            localStorage.setItems('Set-Cookie', cookieArray);*/
      
             login(values.user);
             // console.log('Response:', response.headers);
            })
      } catch (err) {
        console.error(err)
      }
    };
  
  
  return (
 <header className="Login-header">
      
    <Form
      name="normal_login"
      className="login-form"
      onFinish={onFinish}>
      <Form.Item
        name="user"
        rules={[
          {
            required: true,
            message: 'Please input your Username!',
          },
        ]}>
        <Input prefix={<UserOutlined className="site-form-item-icon" />} 
      placeholder="Username" />
      </Form.Item>
      <Form.Item
        name="password"
        rules={[
          {
            required: true,
            message: 'Please input your Password!',
          },
        ]}>
        <Input
          prefix={<LockOutlined className="site-form-item-icon" />}
          type="password"
          placeholder="Password"
        />
      </Form.Item>
      <Form.Item>
      <Button type="primary" htmlType="submit" className="login-form-button">
          Log in
      </Button>
       
      </Form.Item>
    </Form>
    </header> 
  );

} 


export default Login;