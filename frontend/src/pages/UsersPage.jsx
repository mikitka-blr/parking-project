import React, { useState, useEffect } from 'react';
import api from '../api/api';
import { Trash2, Edit2, Plus, User as UserIcon } from 'lucide-react';

export default function UsersPage() {
    const [users, setUsers] = useState([]);
    // убрано поле active — не отображаем плашку/чекбокс активности
    const [form, setForm] = useState({ id: '', fullName: '', email: '' });
    const [isEditing, setIsEditing] = useState(false);
    const [loading, setLoading] = useState(true);

    const fetchUsers = async () => {
        try {
            const response = await api.get('/users');
            if (response.status === 200 || response.status === 204) {
                setUsers(response.data || []);
            }
        } catch (error) {
            console.error('Ошибка загрузки пользователей', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchUsers();
    }, []);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setForm({ ...form, [name]: type === 'checkbox' ? checked : value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (isEditing) {
                await api.put(`/users/${form.id}`, form);
            } else {
                await api.post('/users', form);
            }
            setForm({ id: '', fullName: '', email: '', active: true });
            setIsEditing(false);
            fetchUsers();
        } catch (error) {
            console.error('Ошибка сохранения', error);
            alert('Ошибка при сохранении!');
        }
    };

    const handleEdit = (user) => {
        setForm(user);
        setIsEditing(true);
    };

    const handleDelete = async (id) => {
        if (window.confirm('Вы уверены, что хотите удалить?')) {
            try {
                await api.delete(`/users/${id}`);
                fetchUsers();
            } catch (error) {
                console.error('Ошибка удаления', error);
            }
        }
    };

    return (
        <div>
            <h1 className="page-title">Управление пользователями</h1>

            <div className="card">
                <h2>{isEditing ? 'Редактировать пользователя' : 'Добавить нового'}</h2>
                <form onSubmit={handleSubmit} style={{ display: 'flex', gap: '10px', alignItems: 'center', marginTop: '10px', flexWrap: 'wrap' }}>
                    <input
                        type="text"
                        name="fullName"
                        placeholder="ФИО"
                        value={form.fullName}
                        onChange={handleChange}
                        required
                        style={{ width: '250px', marginBottom: 0 }}
                    />
                    <input
                        type="email"
                        name="email"
                        placeholder="Email"
                        value={form.email}
                        onChange={handleChange}
                        required
                        style={{ width: '250px', marginBottom: 0 }}
                    />
                    {/* Убрали поле активности — в UI не показываем */}
                    <button type="submit" className="btn">
                        {isEditing ? <Edit2 size={16} /> : <Plus size={16} />}
                        {isEditing ? 'Сохранить' : 'Добавить'}
                    </button>
                    {isEditing && (
                        <button type="button" className="btn" onClick={() => { setForm({ id: '', fullName: '', email: '', active: true }); setIsEditing(false); }} style={{ background: '#ddd' }}>
                            Отмена
                        </button>
                    )}
                </form>
            </div>

            <div className="card table-container">
                {loading ? <p>Загрузка...</p> : (
                    <table>
                        <thead>
                            <tr>
                                <th>Пользователь</th>
                                <th>Email</th>
                                <th>Действия</th>
                            </tr>
                        </thead>
                        <tbody>
                            {users.map(user => (
                                <tr key={user.id}>
                                    <td>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                                            <UserIcon size={16} style={{ color: '#888' }} />
                                            {user.fullName}
                                        </div>
                                    </td>
                                        <td>{user.email}</td>
                                            <td style={{ display: 'flex', gap: '10px' }}>
                                        <button className="btn" style={{ padding: '6px 10px' }} onClick={() => handleEdit(user)}>
                                            <Edit2 size={14} />
                                        </button>
                                        <button className="btn btn-danger" style={{ padding: '6px 10px' }} onClick={() => handleDelete(user.id)}>
                                            <Trash2 size={14} />
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            {users.length === 0 && (
                                <tr>
                                    <td colSpan="4" style={{ textAlign: 'center' }}>Нет данных</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
}
