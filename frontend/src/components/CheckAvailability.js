import React, { useState } from "react";

function CheckAvailability({
                               setStartDateTime,
                               setNumberOfDays,
                               selectedCarType,
                               setSelectedCarType
                           }) {
    const [dateTime, setDateTime] = useState("");
    const [days, setDays] = useState("");

    const [availableCars, setAvailableCars] = useState([]);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");

    const findAvailableCars = () => {
        if (!dateTime || !days) {
            setMessage("Please select a date/time and number of days.");
            return;
        }

        setLoading(true);
        setMessage("");

        const url =
            `http://localhost:7070/api/availability` +
            `?startDateTime=${encodeURIComponent(dateTime)}` +
            `&numberOfDays=${days}`;

        fetch(url)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Could not check availability");
                }

                return response.json();
            })
            .then(data => {
                setAvailableCars(data);

                // Send only the information that MakeReservation needs
                setStartDateTime(dateTime);
                setNumberOfDays(days);
                setSelectedCarType("");

                if (data.length === 0) {
                    setMessage("No cars are available for this period.");
                }
            })
            .catch(error => {
                console.error("Error loading available cars:", error);
                setMessage("Could not check availability.");
            })
            .finally(() => {
                setLoading(false);
            });
    };

    return (
        <div className="card shadow-sm">
            <div className="card-body p-4">

                <h2 className="card-title mb-4">
                    Check Availability
                </h2>

                <div className="row g-3">

                    <div className="col-md-6">
                        <label className="form-label">
                            Start date and time
                        </label>

                        <input
                            type="datetime-local"
                            className="form-control"
                            value={dateTime}
                            onChange={event => setDateTime(event.target.value)}
                        />
                    </div>

                    <div className="col-md-3">
                        <label className="form-label">
                            Number of days
                        </label>

                        <input
                            type="number"
                            className="form-control"
                            min="1"
                            value={days}
                            onChange={event => setDays(event.target.value)}
                        />
                    </div>

                    <div className="col-md-3 d-flex align-items-end">
                        <button
                            className="btn btn-primary w-100"
                            onClick={findAvailableCars}
                            disabled={loading}
                        >
                            {loading ? "Checking..." : "Find Available Cars"}
                        </button>
                    </div>

                </div>

                {message && (
                    <div className="alert alert-warning mt-4 mb-0">
                        {message}
                    </div>
                )}

                {availableCars.length > 0 && (
                    <div className="mt-4">

                        <h5>Available Car Types</h5>

                        <select
                            className="form-select"
                            value={selectedCarType}
                            onChange={event =>
                                setSelectedCarType(event.target.value)
                            }
                        >
                            <option value="">
                                Select a car type
                            </option>

                            {availableCars.map(carType => (
                                <option
                                    key={carType.carTypeId}
                                    value={carType.carTypeId}
                                >
                                    {carType.carTypeName} -{" "}
                                    {carType.availableCount} available
                                </option>
                            ))}
                        </select>

                    </div>
                )}

            </div>
        </div>
    );
}

export default CheckAvailability;