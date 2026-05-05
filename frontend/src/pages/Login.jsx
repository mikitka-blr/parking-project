import React, { useState, useEffect } from 'react';
import api from '../api/api';
import { useNavigate } from 'react-router-dom';

export default function Login() {
    const [users, setUsers] = useState([]);
    const [selectedUserId, setSelectedUserId] = useState('');
    const [adminPassword, setAdminPassword] = useState('');
    const [showRegister, setShowRegister] = useState(false);
    const [regForm, setRegForm] = useState({ fullName: '', email: '' });
    const [emailInput, setEmailInput] = useState('');
    const [loginInput, setLoginInput] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        loadUsers();
    }, []);

    const loadUsers = () => {
        api.get('/users').then(res => { if (res.data) setUsers(res.data); }).catch(() => {});
    };

    const loginAsUser = async () => {
        try {
            let userId = selectedUserId;
            if (!userId) {
                // try find by email or login
                const byEmail = emailInput && users.find(u => String(u.email).toLowerCase() === emailInput.trim().toLowerCase());
                const byLogin = loginInput && users.find(u => String(u.fullName).toLowerCase() === loginInput.trim().toLowerCase());
                const found = byEmail || byLogin;
                if (found) {
                    userId = found.id;
                } else {
                    alert('Пользователь не найден. Пожалуйста, выберите из списка или зарегистрируйтесь.');
                    return;
                }
            }
            localStorage.setItem('currentUserId', String(userId));
            localStorage.setItem('isAdmin', 'false');
            navigate('/app/reservations');
        } catch (err) {
            console.error(err);
            alert('Ошибка при входе');
        }
    };

    const registerAndLogin = async () => {
        if (!regForm.fullName || !regForm.email) { alert('Заполните имя и email'); return; }
        try {
            const resp = await api.post('/users', { fullName: regForm.fullName, email: regForm.email });
            // обновим список и залогиним
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

    const loginAsAdmin = () => {
        if (adminPassword === '12345') {
            localStorage.setItem('isAdmin', 'true');
            localStorage.removeItem('currentUserId');
            navigate('/app');
        } else {
            alert('Неверный пароль администратора');
        }
    };

    return (
        <div style={{ maxWidth: 960, margin: '40px auto', position: 'relative' }}>
            <button className="btn" style={{ position: 'absolute', right: 0, top: 0, margin: 12, padding: '6px 10px' }} onClick={() => {
                // show small admin prompt
                const pw = window.prompt('Введите пароль администратора:');
                if (!pw) return;
                if (pw === '12345') { localStorage.setItem('isAdmin', 'true'); localStorage.removeItem('currentUserId'); navigate('/app'); } else { alert('Неверный пароль'); }
            }}>Войти как админ</button>

            <h1 className="page-title" style={{ textAlign: 'center' }}>Добро пожаловать в AutoParking</h1>
            <div style={{ display: 'flex', gap: 20, marginTop: 20, justifyContent: 'center' }}>
                <div className="card" style={{ width: 600, padding: 28 }}>
                    <h2 style={{ marginTop: 0 }}>Войти как пользователь</h2>
                    <div style={{ display: 'flex', gap: 10, alignItems: 'center', marginBottom: 12 }}>
                                                        <select value={selectedUserId} onChange={e => {
                                                            const val = e.target.value;
                                                            setSelectedUserId(val);
                                                            const found = users.find(u => String(u.id) === String(val));
                                                            if (found) {
                                                                setEmailInput(found.email || '');
                                                                setLoginInput(found.fullName || '');
                                                            }
                                                        }} style={{ flex: 1 }}>
                                                            <option value="">-- Выберите пользователя --</option>
                                                            {users.map(u => <option key={u.id} value={u.id}>{u.fullName} ({u.email})</option>)}
                                                        </select>
                    </div>
                    <div style={{ borderTop: '1px solid #eee', paddingTop: 12 }}>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: 8, alignItems: 'center' }}>
                                                            <input placeholder="Email" value={emailInput} onChange={e => setEmailInput(e.target.value)} style={{ width: '100%' }} />
                                                            <input placeholder="Логин" value={loginInput} onChange={e => setLoginInput(e.target.value)} style={{ width: '100%' }} />
                                                            <div style={{ width: '100%', display: 'flex', justifyContent: 'center', marginTop: 8 }}>
                                                                <button className="btn" onClick={loginAsUser} style={{ fontSize: 18, padding: '10px 20px', minWidth: 220 }}>Войти</button>
                                                            </div>
                                                            <a href="/register" onClick={(e) => { e.preventDefault(); navigate('/register'); }} style={{ marginTop: 6, fontSize: 12, color: '#007bff', cursor: 'pointer', textDecoration: 'none' }} onMouseEnter={e => e.currentTarget.style.textDecoration='underline'} onMouseLeave={e => e.currentTarget.style.textDecoration='none'}>Зарегистрироваться</a>
                        </div>
                        {showRegister && (
                            <div style={{ marginTop: 10, display: 'none' }} />
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

