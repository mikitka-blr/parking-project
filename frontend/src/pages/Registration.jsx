import React, { useState } from 'react';
import api from '../api/api';
import { useNavigate } from 'react-router-dom';

export default function Registration() {
    const [form, setForm] = useState({ fullName: '', email: '' });
    const navigate = useNavigate();

    const register = async () => {
        if (!form.email || !form.fullName) { alert('Заполните ФИО и Email'); return; }
        try {
            const resp = await api.post('/users', form);
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
                    <div style={{ display: 'flex', gap: 10 }}>
                        <button className="btn" onClick={register}>Создать и войти</button>
                        <button className="btn btn-danger" onClick={() => navigate('/')}>Отмена</button>
                    </div>
                </div>
            </div>
        </div>
    );
}

