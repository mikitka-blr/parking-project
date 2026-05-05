import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import UsersPage from './pages/UsersPage';
import ReservationsPage from './pages/ReservationsPage';
import SlotsPage from './pages/SlotsPage';
import Login from './pages/Login';
import Registration from './pages/Registration';

function App() {
  return (
    <Routes>
      <Route path='/' element={<Login />} />
      <Route path='/register' element={<Registration />} />
      <Route path='/app' element={<Layout />}>
        <Route index element={<UsersPage />} />
        <Route path='slots' element={<SlotsPage />} />
        <Route path='reservations' element={<ReservationsPage />} />
      </Route>
    </Routes>
  );
}

export default App;
