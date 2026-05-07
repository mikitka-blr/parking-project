import React, { useState } from 'react';
import { Eye, EyeOff } from 'lucide-react';
import api from '../api/api';
import { useNavigate } from 'react-router-dom';

export default function Registration() {
    const [form, setForm] = useState({ fullName: '', email: '', password: '', confirmPassword: '' });
    const [showPassword, setShowPassword] = useState(false);
    const navigate = useNavigate();

    const register = async () => {
        if (!form.email || !form.fullName) { alert('Заполните ФИО и Email'); return; }
        if (!form.password) { alert('Введите пароль'); return; }
        if (form.password !== form.confirmPassword) { alert('Пароли не совпадают'); return; }
        try {
            // send only necessary fields
            const payload = { fullName: form.fullName, email: form.email, password: form.password };
            const resp = await api.post('/users', payload);
            const id = resp.data?.id;
            if (id) {
                localStorage.setItem('currentUserId', String(id));
                localStorage.setItem('isAdmin', 'false');
                navigate('/app/reservations');
            } else {
                alert('Пользователь создан, но не удалось получить id. Войдите через список.');
            }
        } catch (err) {
            console.error('Ошибка регистрации', err);
            alert(err.response?.data?.message || 'Ошибка регистрации');
        }
    };

    return (
        <div style={{ maxWidth: 960, margin: '40px auto', position: 'relative' }}>
            <h1 className="page-title" style={{ textAlign: 'center' }}>Регистрация в AutoParking</h1>
            <div style={{ display: 'flex', gap: 20, marginTop: 20, justifyContent: 'center' }}>
                <div className="card" style={{ width: 600, padding: 28 }}>
                    <h2 style={{ marginTop: 0 }}>Регистрация</h2>
                    <div style={{ borderTop: '1px solid #eee', paddingTop: 12 }}>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                            <input placeholder="ФИО" value={form.fullName} onChange={e => setForm({...form, fullName: e.target.value})} style={{ width: '100%', marginBottom: 8 }} />
                            <input placeholder="Email" value={form.email} onChange={e => setForm({...form, email: e.target.value})} style={{ width: '100%', marginBottom: 8 }} />
                            <div style={{ position: 'relative', marginBottom: 8 }}>
                                <input placeholder="Пароль" type={showPassword ? 'text' : 'password'} value={form.password} onChange={e => setForm({...form, password: e.target.value})} style={{ width: '100%', paddingRight: 44, margin: 0 }} />
                                <button type="button" onClick={() => setShowPassword(!showPassword)} style={{ position: 'absolute', right: 7, top: '50%', transform: 'translateY(-50%)', border: 'none', background: 'transparent', padding: 0, display: 'flex', alignItems: 'center', cursor: 'pointer' }} aria-label={showPassword ? 'Скрыть пароль' : 'Показать пароль'} title={showPassword ? 'Скрыть пароль' : 'Показать пароль'}>
                                    {showPassword ? <Eye size={16} /> : <EyeOff size={16} />}
                                </button>
                            </div>
                            <div style={{ position: 'relative', marginBottom: 8 }}>
                                <input placeholder="Подтвердите пароль" type={showPassword ? 'text' : 'password'} value={form.confirmPassword} onChange={e => setForm({...form, confirmPassword: e.target.value})} style={{ width: '100%', margin: 0 }} />
                            </div>
                            <div style={{ width: '100%', display: 'flex', justifyContent: 'center', marginTop: 8 }}>
                                <button className="btn" onClick={register} style={{ fontSize: 18, padding: '10px 20px', minWidth: 220, justifyContent: 'center' }}>Зарегистрироваться</button>
                            </div>
                            <div style={{ textAlign: 'center', marginTop: 6, fontSize: 12 }}>
                                <span>Уже есть аккаунт? </span>
                                <a href="/" onClick={(e) => { e.preventDefault(); navigate('/'); }} style={{ color: '#007bff', cursor: 'pointer', textDecoration: 'none' }} onMouseEnter={e => e.currentTarget.style.textDecoration='underline'} onMouseLeave={e => e.currentTarget.style.textDecoration='none'}>Войти</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

