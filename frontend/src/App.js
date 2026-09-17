import React, { useState } from "react";
import "bootstrap/dist/css/bootstrap.min.css";

import CheckAvailability from "./components/CheckAvailability";
import MakeReservation from "./components/MakeReservation";
import Reservations from "./components/Reservations";

function App() {
    const [startDateTime, setStartDateTime] = useState("");
    const [numberOfDays, setNumberOfDays] = useState("");
    const [selectedCarType, setSelectedCarType] = useState("");
    const [refreshReservations, setRefreshReservations] = useState(0);
    return (
        <div className="bg-light min-vh-100">

            <nav className="navbar navbar-dark bg-primary mb-4">
                <div className="container">
          <span className="navbar-brand mb-0 h1">
            Car Rental
          </span>
                </div>
            </nav>

            <div className="container pb-5">

                <div className="row g-4">

                    <div className="col-12">
                        <CheckAvailability
                            setStartDateTime={setStartDateTime}
                            setNumberOfDays={setNumberOfDays}
                            selectedCarType={selectedCarType}
                            setSelectedCarType={setSelectedCarType}
                        />
                    </div>

                    <div className="col-12">
                        <MakeReservation
                            startDateTime={startDateTime}
                            numberOfDays={numberOfDays}
                            selectedCarType={selectedCarType}
                            onReservationCreated={() =>
                                setRefreshReservations(value => value + 1)
                            }
                        />
                    </div>

                    <div className="col-12">
                        <Reservations refresh={refreshReservations} />                    </div>

                </div>

            </div>
        </div>
    );
}

export default App;