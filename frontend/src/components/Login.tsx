import {Form, Input, Button} from 'antd';
import {  UserOutlined, LockOutlined  }  from  '@ant-design/icons';
import React from 'react';
import './Login.css'

interface LoginFormData {
  username: string;
  password: string;
}

class Login extends React.Component
  {

    render () {
    const onFinish = async (values: LoginFormData) => {
     
      alert(values.username);
     
    };
  
  
  return (
    <header className="Login-header">
      
    <Form
      name="normal_login"
      className="login-form"
      onFinish={onFinish}>
      <Form.Item
        name="username"
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
}

export default Login;