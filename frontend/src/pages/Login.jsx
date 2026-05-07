import React, { useState, useEffect } from 'react';
import api from '../api/api';
import { useNavigate } from 'react-router-dom';
import { Eye, EyeOff } from 'lucide-react';

export default function Login() {
    const [users, setUsers] = useState([]);
    const [emailInput, setEmailInput] = useState('');
    const [passwordInput, setPasswordInput] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [regForm, setRegForm] = useState({ fullName: '', email: '' });
    const navigate = useNavigate();

    useEffect(() => {
        loadUsers();
    }, []);

    const loadUsers = () => {
        api.get('/users').then(res => { if (res.data) setUsers(res.data); }).catch(() => {});
    };

    const login = async () => {
        try {
            const email = (emailInput || '').trim().toLowerCase();
            if (!email) { alert('Введите email'); return; }
            // call backend auth endpoint
            const resp = await api.post('/auth/login', { email, password: passwordInput });
            if (resp.status === 200 && resp.data) {
                const user = resp.data;
                const isAdminUser = (String(user.email).toLowerCase() === 'admin@gmail.com') || (String(user.email).toLowerCase() === 'admin') || (String(user.fullName).toLowerCase() === 'admin');
                if (isAdminUser) {
                    localStorage.setItem('isAdmin', 'true');
                    localStorage.removeItem('currentUserId');
                    navigate('/app');
                } else {
                    localStorage.setItem('currentUserId', String(user.id));
                    localStorage.setItem('isAdmin', 'false');
                    navigate('/app/reservations');
                }
            } else {
                alert('Неверные учётные данные');
            }
        } catch (err) {
            console.error(err);
            alert('Ошибка при входе');
        }
    };

    const registerAndLogin = async () => {
        if (!regForm.fullName || !regForm.email) { alert('Заполните имя и email'); return; }
        try {
            const resp = await api.post('/users', { fullName: regForm.fullName, email: regForm.email });
            loadUsers();
            const newId = resp.data?.id;
            if (newId) {
                localStorage.setItem('currentUserId', String(newId));
                localStorage.setItem('isAdmin', 'false');
                navigate('/app');
            } else {
                alert('Пользователь создан, пожалуйста выберите его в списке');
            }
        } catch (err) {
            console.error('Ошибка регистрации', err);
            alert(err.response?.data?.message || 'Ошибка регистрации');
        }
    };

    return (
        <div style={{ maxWidth: 960, margin: '40px auto', position: 'relative' }}>
            <h1 className="page-title" style={{ textAlign: 'center' }}>Добро пожаловать в AutoParking</h1>
            <div style={{ display: 'flex', gap: 20, marginTop: 20, justifyContent: 'center' }}>
                <div className="card" style={{ width: 600, padding: 28 }}>
                    <h2 style={{ marginTop: 0 }}>Войти</h2>
                    <div style={{ borderTop: '1px solid #eee', paddingTop: 12 }}>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                            <div>
                                <div style={{ marginBottom: 8 }}>
                                    <input placeholder="Email или ФИО" value={emailInput} onChange={e => setEmailInput(e.target.value)} style={{ width: '100%' }} />
                                </div>
                                <div style={{ position: 'relative' }}>
                                    <input placeholder="Пароль" type={showPassword ? 'text' : 'password'} value={passwordInput} onChange={e => setPasswordInput(e.target.value)} style={{ width: '100%', paddingRight: 44 }} />
                                    <button type="button" onClick={() => setShowPassword(!showPassword)} style={{ position: 'absolute', right: 7, top: '40%', transform: 'translateY(-50%)', border: 'none', background: 'transparent', padding: 0, display: 'flex', alignItems: 'center', cursor: 'pointer' }} aria-label={showPassword ? 'Скрыть пароль' : 'Показать пароль'} title={showPassword ? 'Скрыть пароль' : 'Показать пароль'}>
                                        {showPassword ? <Eye size={16} /> : <EyeOff size={16} />}
                                    </button>
                                </div>
                            </div>
                            <div style={{ width: '100%', display: 'flex', justifyContent: 'center', marginTop: 8 }}>
                                <button className="btn" onClick={login} style={{ fontSize: 18, padding: '10px 20px', minWidth: 220, justifyContent: 'center' }}>Войти</button>
                            </div>
                            <div style={{ textAlign: 'center', marginTop: 6 }}>
                                <a href="/register" onClick={(e) => { e.preventDefault(); navigate('/register'); }} style={{ fontSize: 12, color: '#007bff', cursor: 'pointer', textDecoration: 'none' }} onMouseEnter={e => e.currentTarget.style.textDecoration='underline'} onMouseLeave={e => e.currentTarget.style.textDecoration='none'}>Зарегистрироваться</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

