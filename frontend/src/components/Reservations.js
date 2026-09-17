import React, {useEffect, useState} from "react";

function Reservations({ refresh })
{ const [reservations, setReservations] = useState([]);
    const [loading, setLoading] = useState(true);

    const loadReservations = () => {
        setLoading(true);

        fetch("http://localhost:7070/api/reservations")
            .then(response => {
                if (!response.ok) {
                    throw new Error("Could not load reservations");
                }

                return response.json();
            })
            .then(data => {
                setReservations(data);
            })
            .catch(error => {
                console.error("Error loading reservations:", error);
            })
            .finally(() => {
                setLoading(false);
            });
    };

    useEffect(() => {
        loadReservations();
    }, [refresh]);

    return (
        <div className="card shadow-sm">
            <div className="card-body p-4">

                <div className="d-flex justify-content-between align-items-center mb-4">

                    <h2 className="card-title mb-0">
                        Current Reservations
                    </h2>

                    <button
                        className="btn btn-outline-primary btn-sm"
                        onClick={loadReservations}
                    >
                        Refresh
                    </button>

                </div>

                {loading ? (
                    <p className="text-muted mb-0">
                        Loading reservations...
                    </p>
                ) : reservations.length === 0 ? (
                    <p className="text-muted mb-0">
                        No reservations yet.
                    </p>
                ) : (
                    <div className="table-responsive">

                        <table className="table table-hover align-middle mb-0">

                            <thead className="table-light">
                            <tr>
                                <th>Reservation</th>
                                <th>Car</th>
                                <th>Client ID</th>
                                <th>Client Full name </th>
                                <th>Car Type</th>
                                <th>Start</th>
                                <th>End</th>
                            </tr>
                            </thead>

                            <tbody>
                            {reservations.map(reservation => (
                                <tr key={reservation.reservationId}>
                                    <td>#{reservation.reservationId}</td>
                                    <td>Car {reservation.carId}</td>
                                    <td>Client {reservation.clientId}</td>

                                    <td>
                                        {reservation.clientFirstName} {reservation.clientLastName}
                                    </td>
                                    <td>
                                        {reservation.carTypeName}
                                    </td>
                                    <td>{reservation.startDateTime}</td>
                                    <td>{reservation.endDateTime}</td>
                                </tr>
                            ))}
                            </tbody>

                        </table>

                    </div>
                )}

            </div>
        </div>
    );
}

export default Reservations;