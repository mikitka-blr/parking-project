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
        <div style={{ maxWidth: 700, margin: '40px auto' }}>
            <h1 className="page-title" style={{ textAlign: 'center' }}>Регистрация</h1>
            <div className="card" style={{ padding: 20 }}>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                    <input placeholder="ФИО" value={form.fullName} onChange={e => setForm({...form, fullName: e.target.value})} />
                    <input placeholder="Email" value={form.email} onChange={e => setForm({...form, email: e.target.value})} />
                    <div style={{ position: 'relative' }}>
                        <input placeholder="Пароль" type={showPassword ? 'text' : 'password'} value={form.password} onChange={e => setForm({...form, password: e.target.value})} style={{ width: '100%', marginBottom: 8, paddingRight: 40 }} />
                        <input placeholder="Подтвердите пароль" type={showPassword ? 'text' : 'password'} value={form.confirmPassword} onChange={e => setForm({...form, confirmPassword: e.target.value})} style={{ width: '100%' }} />
                        <button type="button" onClick={() => setShowPassword(!showPassword)} style={{ position: 'absolute', right: 8, top: 36, border: 'none', background: 'transparent', padding: 4, cursor: 'pointer' }} aria-label="toggle password">
                            {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                        </button>
                    </div>
                    <div style={{ display: 'flex', gap: 10 }}>
                        <button className="btn" onClick={register}>Создать и войти</button>
                        <button className="btn btn-danger" onClick={() => navigate('/')}>Отмена</button>
                    </div>
                </div>
            </div>
        </div>
    );
}

