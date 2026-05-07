import React, { useState, useEffect } from 'react';
import api from '../api/api';
import { Car, AlertCircle, CheckCircle } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function SlotsPage() {
    const [slots, setSlots] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filterType, setFilterType] = useState('ALL');
    const [filterOccupied, setFilterOccupied] = useState('ALL');
    const [isAdmin, setIsAdmin] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 10;
    const navigate = useNavigate();

    const fetchSlots = async () => {
        setLoading(true);
        try {
            const response = await api.get('/demo/slots');
            if (response.status === 200 || response.status === 204) {
                setSlots(response.data || []);
            }
        } catch (error) {
            console.error('Ошибка загрузки мест', error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchSlots();
    }, []);

    useEffect(() => {
        const admin = localStorage.getItem('isAdmin') === 'true';
        setIsAdmin(admin);
    }, []);

    useEffect(() => {
        setCurrentPage(1);
    }, [filterType, filterOccupied]);

    const handleFree = async (id) => {
        if (!isAdmin) {
            const pw = window.prompt('Введите пароль администратора для освобождения места:');
            if (pw !== '12345') { alert('Неверный пароль'); return; }
            setIsAdmin(true);
        }
        if(window.confirm('Освободить место?')) {
            await api.post(`/demo/free-slot/${id}`);
            fetchSlots(); // Перезапрашиваем данные
        }
    };

    // admin login removed from this page; admin must login via main Login page

    const filteredSlots = slots.filter(s => {
        // support different property names and case variations coming from backend
        const rawType = s?.type || s?.slotType || s?.typeName || '';
        const slotTypeNormalized = String(rawType).toUpperCase();
        if (filterType !== 'ALL' && slotTypeNormalized !== filterType) return false;
        if (filterOccupied === 'FREE' && s.occupied) return false;
        if (filterOccupied === 'OCCUPIED' && !s.occupied) return false;
        return true;
    });

    const totalPages = Math.ceil(filteredSlots.length / itemsPerPage);
    const indexOfLastSlot = currentPage * itemsPerPage;
    const indexOfFirstSlot = indexOfLastSlot - itemsPerPage;
    const currentSlots = filteredSlots.slice(indexOfFirstSlot, indexOfLastSlot);

    return (
        <div>
            <h1 className="page-title">Все парковочные места</h1>

            <div className="card">
                <div style={{ display: 'flex', gap: '10px', alignItems: 'center', marginBottom: '12px' }}>
                    <label>Тип:
                        <select value={filterType} onChange={e => setFilterType(e.target.value)} style={{ marginLeft: '6px' }}>
                            <option value="ALL">Все</option>
                            <option value="REGULAR">Обычные</option>
                            <option value="DISABLED">Для инвалидов</option>
                            <option value="ELECTRIC">Электро</option>
                        </select>
                    </label>
                    <label>Статус:
                        <select value={filterOccupied} onChange={e => setFilterOccupied(e.target.value)} style={{ marginLeft: '6px' }}>
                            <option value="ALL">Все</option>
                            <option value="FREE">Свободные</option>
                            <option value="OCCUPIED">Занятые</option>
                        </select>
                    </label>
                    {/* admin login removed from this page; use main Login page to enter as admin */}
                </div>
                {loading ? <p>Загрузка...</p> : (
                    <>
                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '20px' }}>
                            {currentSlots.map(slot => (
                                <div
                                    key={slot.id}
                                style={{
                                    border: '1px solid #ccc',
                                    borderRadius: '8px',
                                    padding: '20px',
                                    width: '280px',
                                    backgroundColor: slot.occupied ? '#fffdf5' : '#f8fff9',
                                    borderTop: `4px solid ${slot.occupied ? '#dc3545' : '#28a745'}`
                                }}
                            >
                                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '10px' }}>
                                    <h3 style={{ margin: 0, display: 'flex', alignItems: 'center', gap: '8px' }}>
                                        <Car size={20} />
                                        Место №{slot.number || ''}
                                    </h3>
                                    {slot.occupied ?
                                        <AlertCircle color="#dc3545" size={24} /> :
                                        <CheckCircle color="#28a745" size={24} />
                                    }
                                </div>
                                <p style={{ margin: '10px 0', fontSize: '15px' }}>
                                    <strong>Статус: </strong>
                                    <span style={{ color: slot.occupied ? '#dc3545' : '#28a745', fontWeight: 'bold' }}>
                                        {slot.occupied ? 'Занято' : 'Свободно'}
                                    </span>
                                </p>
                                <p style={{ margin: '10px 0', fontSize: '13px', color: '#666' }}>
                                    Тип: {slot.type === 'REGULAR' ? 'Обычное' :
                                          slot.type === 'DISABLED' ? 'Для инвалидов' :
                                          slot.type === 'ELECTRIC' ? 'Для электрокаров' : 'Универсальное'}
                                </p>

                                {slot.occupied && (
                                    <button
                                        className="btn btn-danger"
                                        style={{ width: '100%', marginTop: '10px', display: 'flex', justifyContent: 'center' }}
                                        onClick={() => handleFree(slot.id)}
                                    >
                                        Освободить место
                                    </button>
                                )}
                                {!slot.occupied && (
                                    <button className="btn" style={{ width: '100%', marginTop: '10px', display: 'flex', justifyContent: 'center' }} onClick={() => navigate('/app/reservations', { state: { slotId: slot.id } })}>
                                        Забронировать
                                    </button>
                                )}
                            </div>
                        ))}
                        </div>

                        {totalPages > 1 && (
                            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '15px', marginTop: '25px', width: '100%' }}>
                                <button
                                    className="btn"
                                    style={currentPage === 1 ? { background: '#ccc', cursor: 'not-allowed' } : {}}
                                    disabled={currentPage === 1}
                                    onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
                                >
                                    &larr; Назад
                                </button>
                                <span style={{ fontWeight: 'bold' }}>
                                    Страница {currentPage} из {totalPages}
                                </span>
                                <button
                                    className="btn"
                                    style={currentPage === totalPages ? { background: '#ccc', cursor: 'not-allowed' } : {}}
                                    disabled={currentPage === totalPages}
                                    onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
                                >
                                    Вперед &rarr;
                                </button>
                            </div>
                        )}
                    </>
                )}
            </div>
        </div>
    );
}
