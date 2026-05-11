import React, { useState, useEffect } from 'react';
import api from '../api/api';
import { Search, Calendar, MapPin, Settings, Plus, Trash2 } from 'lucide-react';
import { useLocation } from 'react-router-dom';

export default function ReservationsPage() {
    const [searchName, setSearchName] = useState('');
    const [users, setUsers] = useState([]);
    const [selectedUserId, setSelectedUserId] = useState('');
    const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(false);
    const [editingResId, setEditingResId] = useState(null);
    const [editForm, setEditForm] = useState({ startTime: '', endTime: '', serviceIds: [] });
    const location = useLocation();

    const [availableSlots, setAvailableSlots] = useState([]);
    const [extraServices, setExtraServices] = useState([]);
    const [form, setForm] = useState({
        slotId: '',
        startTime: '',
        endTime: '',
        serviceIds: []
    });

    useEffect(() => {
        api.get('/users').then(res => {
            if (res.data) setUsers(res.data);
        }).catch(err => console.error(err));

        fetchAvailableSlots();
        fetchExtraServices();
        const pre = location.state?.slotId;
        const cur = localStorage.getItem('currentUserId');
        if (cur) setSelectedUserId(cur);
        if (pre) setForm(f => ({ ...f, slotId: pre }));
    }, []);

    const fetchAvailableSlots = () => {
        api.get('/demo/slots/available').then(res => {
            if (res.data) setAvailableSlots(res.data);
        }).catch(err => console.error('Ошибка загрузки свободных мест', err));
    };

    const fetchUserReservations = async (userId) => {
        if (!userId) return;
        setLoading(true);
        try {
            const url = userId === 'ALL' ? '/demo/reservations' : `/demo/users/${userId}/reservations`;
            const response = await api.get(url);
            if (response.status === 200 || response.status === 204) {
                setReservations(Array.isArray(response.data) ? response.data : []);
            }
        } catch (error) {
            console.error('Ошибка поиска бронирований', error);
            setReservations([]);
        } finally {
            setLoading(false);
        }
    };

    const fetchExtraServices = async () => {
        try {
            const response = await api.get('/demo/services');
            if (response.status === 200) {
                setExtraServices(response.data || []);
            }
        } catch (error) {
            console.error('Ошибка загрузки дополнительных услуг', error);
        }
    };

    useEffect(() => {
        fetchUserReservations(selectedUserId);
    }, [selectedUserId]);

    useEffect(() => {
        const isAdmin = localStorage.getItem('isAdmin') === 'true';
        const cur = localStorage.getItem('currentUserId');
        if (isAdmin) {
            setSelectedUserId('ALL');
        } else if (cur) {
            setSelectedUserId(cur);
        }
    }, []);

    const handleSearch = (e) => {
        e?.preventDefault();
        fetchUserReservations(selectedUserId);
    };

    const formatDate = (dateObj) => {
        if (!dateObj) return '';
        let d;
        if (Array.isArray(dateObj)) {
            const [y, m, day, h=0, min=0] = dateObj;
            d = new Date(y, m-1, day, h, min);
        } else {
            d = new Date(dateObj);
        }
        return d.toLocaleString('ru-RU', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' });
    };

    const handleBooking = async (e) => {
        e.preventDefault();
        if (!selectedUserId) {
            alert('Сначала выберите пользователя для бронирования в панели поиска!');
            return;
        }

        try {
            const bookingRequest = {
                userId: Number(selectedUserId),
                slotId: Number(form.slotId),
                startTime: new Date(form.startTime).toISOString(),
                endTime: new Date(form.endTime).toISOString(),
                serviceIds: form.serviceIds
            };

            await api.post('/demo/book', bookingRequest);
            alert('Бронь успешно создана!');
            setForm({ slotId: '', startTime: '', endTime: '', serviceIds: [] });
            fetchAvailableSlots();
            fetchUserReservations(selectedUserId);
        } catch (error) {
            console.error('Ошибка при бронировании', error);
            alert(error.response?.data?.message || 'Ошибка создания брони (возможно место уже занято)');
        }
    };

    const handleFreeSlot = async (slotId) => {
        console.log('handleFreeSlot called for slotId=', slotId);
        if (!window.confirm('Вы уверены, что хотите отменить эту бронь и освободить место?')) return;
        console.log('User confirmed free-slot for slotId=', slotId);
        try {
            const resp = await api.post(`/demo/free-slot/${slotId}`);
            console.log('POST /demo/free-slot response', resp);
            fetchAvailableSlots();
            fetchUserReservations(selectedUserId);
        } catch (error) {
            console.error('Ошибка при освобождении места', error, error?.response);
            alert('Ошибка при отмене брони: ' + (error?.response?.data?.message || error.message));
        }
    };

    const formatForInput = (dateObj) => {
        if (!dateObj) return '';
        let d;
        if (Array.isArray(dateObj)) {
            const [y, m, day, h=0, min=0, s=0] = dateObj;
            d = new Date(y, m-1, day, h, min, s);
        } else {
            d = new Date(dateObj);
        }
        const offset = d.getTimezoneOffset() * 60000;
        return new Date(d.getTime() - offset).toISOString().slice(0, 16);
    };

    const handleEditClick = (res) => {
        setEditingResId(res.id);
        setEditForm({
            startTime: formatForInput(res.startTime),
            endTime: formatForInput(res.endTime),
            serviceIds: res.services?.map(s => s.id) || []
        });
    };

    const handleSaveEdit = async (res) => {
        try {
            const bookingRequest = {
                userId: res.user?.id ? Number(res.user.id) : Number(selectedUserId),
                slotId: Number(res.slot?.id || res.slotId),
                startTime: new Date(editForm.startTime).toISOString(),
                endTime: new Date(editForm.endTime).toISOString(),
                serviceIds: editForm.serviceIds || []
            };
            await api.post(`/demo/book/${res.id}`, bookingRequest);
            alert('Бронь успешно обновлена!');
            setEditingResId(null);
            fetchUserReservations(selectedUserId);
        } catch (error) {
            console.error('Ошибка при обновлении брони', error);
            alert('Ошибка обновления брони');
        }
    };

    const handleCancelEdit = () => {
        setEditingResId(null);
    };

    return (
        <div>
            <h1 className="page-title">Бронирования</h1>

            <div className="card">
                <h2>Клиент</h2>
                <form onSubmit={handleSearch} style={{ display: 'flex', gap: '10px', marginTop: '10px' }}>
                    {localStorage.getItem('isAdmin') === 'true' ? (
                        <>
                            <select
                                value={selectedUserId}
                                onChange={(e) => setSelectedUserId(e.target.value)}
                                style={{ width: '300px', marginBottom: 0 }}
                            >
                                <option value="ALL">-- Показать все брони --</option>
                                {users.map(u => (
                                    <option key={u.id} value={u.id}>{u.fullName} ({u.email})</option>
                                ))}
                            </select>
                            <button type="submit" className="btn">
                                <Search size={16} /> Показать брони
                            </button>
                        </>
                    ) : (
                        <div style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
                            <div style={{ padding: '8px 12px', border: '1px solid #ddd', borderRadius: 4 }}>{users.find(u => String(u.id) === selectedUserId)?.fullName || 'Пользователь'}</div>
                        </div>
                    )}
                </form>
            </div>

            {selectedUserId && selectedUserId !== 'ALL' && (
                <div className="card" style={{ backgroundColor: '#fffdf5', borderLeft: '4px solid var(--primary-yellow)' }}>
                    <h2>Создать бронь</h2>
                    <form onSubmit={handleBooking} style={{ display: 'flex', gap: '10px', marginTop: '10px', flexWrap: 'wrap', alignItems: 'center' }}>
                        <select
                            value={form.slotId}
                            onChange={(e) => setForm({...form, slotId: e.target.value})}
                            required
                            style={{ width: '200px', marginBottom: 0 }}
                        >
                            <option value="">-- Свободные места --</option>
                            {availableSlots.map(slot => (
                                <option key={slot.id} value={slot.id}>Место № {slot.number || ''}</option>
                            ))}
                        </select>
                        <input
                            type="datetime-local"
                            value={form.startTime}
                            onChange={(e) => setForm({...form, startTime: e.target.value})}
                            required
                            style={{ width: '200px', marginBottom: 0 }}
                        />
                        <span>по</span>
                        <input
                            type="datetime-local"
                            value={form.endTime}
                            onChange={(e) => setForm({...form, endTime: e.target.value})}
                            required
                            style={{ width: '200px', marginBottom: 0 }}
                        />
                        <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
                            <Settings size={16}/> <span>Услуги:</span>
                            {extraServices.map(srv => (
                                <label key={srv.id} style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                                    <input
                                        type="checkbox"
                                        checked={form.serviceIds.includes(srv.id)}
                                        onChange={(e) => {
                                            if (e.target.checked) {
                                                setForm({...form, serviceIds: [...form.serviceIds, srv.id]});
                                            } else {
                                                setForm({...form, serviceIds: form.serviceIds.filter(id => id !== srv.id)});
                                            }
                                        }}
                                        style={{ margin: 0, width: 'auto', cursor: 'pointer' }}
                                    /> {srv.name}
                                </label>
                            ))}
                        </div>
                        <button type="submit" className="btn" style={{ background: '#28a745', color: '#fff' }}>
                            <Plus size={16} /> Создать бронь
                        </button>
                    </form>
                </div>
            )}

            <div className="card table-container">
                {loading ? <p>Поиск...</p> : (
                    <table>
                        <thead>
                            <tr>
                                <th><MapPin size={16} style={{display: 'inline', verticalAlign: 'middle', marginRight: '4px'}}/> Место</th>
                                <th><Calendar size={16} style={{display: 'inline', verticalAlign: 'middle', marginRight: '4px'}}/> Время</th>
                                <th><Settings size={16} style={{display: 'inline', verticalAlign: 'middle', marginRight: '4px'}}/> Услуги</th>
                                <th>Действия</th>
                            </tr>
                        </thead>
                        <tbody>
                            {reservations.map(res => (
                                <tr key={res.id}>
                                    <td>
                                        <strong>{res.slot?.number || ''}</strong>
                                        {selectedUserId === 'ALL' && res.user && (
                                            <div style={{ fontSize: '13px', color: '#555', marginTop: '4px' }}>
                                                {res.user.fullName || res.user.email}
                                            </div>
                                        )}
                                    </td>
                                    <td>
                                        {editingResId === res.id ? (
                                            <div style={{display: 'flex', flexDirection: 'column', gap: '5px'}}>
                                                <input
                                                    type="datetime-local"
                                                    value={editForm.startTime}
                                                    onChange={e => setEditForm({...editForm, startTime: e.target.value})}
                                                />
                                                <input
                                                    type="datetime-local"
                                                    value={editForm.endTime}
                                                    onChange={e => setEditForm({...editForm, endTime: e.target.value})}
                                                />
                                            </div>
                                        ) : (
                                            <>
                                                {formatDate(res.startTime)} <br/>
                                                — {formatDate(res.endTime)}
                                            </>
                                        )}
                                    </td>
                                    <td>
                                        {editingResId === res.id ? (
                                            <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
                                                {extraServices.map(srv => (
                                                    <label key={srv.id} style={{ display: 'flex', alignItems: 'center', gap: '5px', fontSize: '13px' }}>
                                                        <input
                                                            type="checkbox"
                                                            checked={editForm.serviceIds?.includes(srv.id) || false}
                                                            onChange={(e) => {
                                                                if (e.target.checked) {
                                                                    setEditForm({...editForm, serviceIds: [...(editForm.serviceIds || []), srv.id]});
                                                                } else {
                                                                    setEditForm({...editForm, serviceIds: (editForm.serviceIds || []).filter(id => id !== srv.id)});
                                                                }
                                                            }}
                                                            style={{ margin: 0, width: 'auto', cursor: 'pointer' }}
                                                        /> {srv.name} (₽{srv.price})
                                                    </label>
                                                ))}
                                            </div>
                                        ) : res.services && res.services.length > 0 ? (
                                            <ul style={{ margin: 0, paddingLeft: '20px' }}>
                                                {res.services.map(srv => (
                                                    <li key={srv.id}>{srv.name} (₽{srv.price})</li>
                                                ))}
                                            </ul>
                                        ) : (
                                            <span style={{ color: '#888' }}>Нет доп. услуг</span>
                                        )}
                                    </td>
                                    <td>
                                        {editingResId === res.id ? (
                                            <div style={{ display: 'flex', gap: '5px', alignItems: 'center' }}>
                                                <button type="button" className="btn" style={{background: '#28a745', color: 'white', padding: '6px 10px', fontSize: '12px', margin: 0}} onClick={() => handleSaveEdit(res)}>Сохранить</button>
                                                <button type="button" className="btn btn-danger" style={{padding: '6px 10px', fontSize: '12px', margin: 0}} onClick={handleCancelEdit}>Отмена</button>
                                            </div>
                                        ) : (
                                            <div style={{ display: 'flex', gap: '5px', alignItems: 'center' }}>
                                                <button type="button" className="btn" style={{padding: '6px 10px', fontSize: '12px', margin: 0}} onClick={() => handleEditClick(res)}>Редактировать</button>
                                                <button type="button"
                                                    className="btn btn-danger"
                                                    onClick={() => handleFreeSlot(res.slot?.id || res.slotId)}
                                                    style={{ padding: '6px 10px', fontSize: '12px', display: 'inline-flex', alignItems: 'center', gap: '4px', margin: 0 }}
                                                >
                                                    <Trash2 size={14} /> Отменить
                                                </button>
                                            </div>
                                        )}
                                    </td>
                                </tr>
                            ))}
                            {reservations.length === 0 && selectedUserId !== 'ALL' && (
                                <tr>
                                    <td colSpan="4" style={{ textAlign: 'center' }}>У этого пользователя пока нет бронирований. Забронируйте место выше!</td>
                                </tr>
                            )}
                            {reservations.length === 0 && selectedUserId === 'ALL' && (
                                <tr>
                                    <td colSpan="4" style={{ textAlign: 'center' }}>В системе нет ни одного бронирования.</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
}
