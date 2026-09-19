import React, {useEffect, useState} from "react";

function MakeReservation({
                             startDateTime,
                             numberOfDays,
                             selectedCarType,
                             onReservationCreated
                         }) {
    const [clients, setClients] = useState([]);
    const [selectedClient, setSelectedClient] = useState("");

    const [message, setMessage] = useState("");
    const [success, setSuccess] = useState(false);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        fetch("http://localhost:7070/api/clients")
            .then(response => response.json())
            .then(data => setClients(data))
            .catch(error =>
                console.error("Error loading clients:", error)
            );
    }, []);

    const createReservation = async () => {
        setLoading(true);
        setMessage("");
        setSuccess(false);

        const request = {
            clientId: Number(selectedClient),
            carTypeId: Number(selectedCarType),
            startDateTime: startDateTime,
            numberOfDays: Number(numberOfDays)
        };

        try {
            const response = await fetch(
                "http://localhost:7070/api/reservations",
                {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(request)
                }
            );

            const responseBody = await response.text();

            if (!response.ok) {
                throw new Error(
                    responseBody || "Reservation failed"
                );
            }

            const reservation = JSON.parse(responseBody);

            setSuccess(true);

            setMessage(
                `Reservation created successfully. Car ${reservation.carId} reserved.`
            );

            setSelectedClient("");
            onReservationCreated();

        } catch (error) {
            console.error("Error creating reservation:", error);
            setMessage("Reservation failed: " + error.message);

        } finally {
            setLoading(false);
        }
    };

    const canReserve =
        startDateTime &&
        numberOfDays &&
        selectedCarType &&
        selectedClient;

    return (
        <div className="card shadow-sm">
            <div className="card-body p-4">

                <h2 className="card-title mb-4">
                    Make Reservation
                </h2>

                {!startDateTime || !numberOfDays ? (
                    <div className="alert alert-secondary mb-0">
                        Check availability first.
                    </div>
                ) : (
                    <div className="row g-3">

                        <div className="col-md-6">
                            <label className="form-label">
                                Client
                            </label>

                            <select
                                className="form-select"
                                value={selectedClient}
                                onChange={event =>
                                    setSelectedClient(event.target.value)
                                }
                            >
                                <option value="">
                                    Select a client
                                </option>

                                {clients.map(client => (
                                    <option
                                        key={client.id}
                                        value={client.id}
                                    >
                                        {client.firstName} {client.lastName}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="col-md-6">
                            <label className="form-label">
                                Car Type
                            </label>

                            <input
                                type="text"
                                className="form-control"
                                value={selectedCarType ? "Selected" : ""}
                                placeholder="Select a car type above"
                                disabled
                                readOnly
                            />
                        </div>

                        <div className="col-12">
                            <button
                                className="btn btn-success"
                                onClick={createReservation}
                                disabled={!canReserve || loading}
                            >
                                {loading ? "Reserving..." : "Reserve"}
                            </button>
                        </div>

                    </div>
                )}

                {message && (
                    <div
                        className={`alert ${
                            success
                                ? "alert-success"
                                : "alert-danger"
                        } mt-4 mb-0`}
                    >
                        {message}
                    </div>
                )}

            </div>
        </div>
    );
}

export default MakeReservation;