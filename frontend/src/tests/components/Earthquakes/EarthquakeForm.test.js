import { render, waitFor, fireEvent } from "@testing-library/react";
import EarthquakeForm from "main/components/Earthquakes/EarthquakeForm";
import { earthquakeFixtures } from "fixtures/earthquakeFixtures";
import { BrowserRouter as Router } from "react-router-dom";

const mockedNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockedNavigate
}));


describe("EarthquakeForm tests", () => {

    test("renders correctly ", async () => {

        const { getByText } = render(
            <Router  >
                <EarthquakeForm />
            </Router>
        );
       
        await waitFor(() => expect(getByText(/Retrieve/)).toBeInTheDocument());
    });


    test("renders correctly when passing in a Earthquake ", async () => {

        const { getByText, getByTestId } = render(
            <Router  >
                <EarthquakeForm initialEarthquake={earthquakeFixtures.oneDate} />
            </Router>
        );
        await waitFor(() => expect(getByTestId(/EarthquakeForm-distance/)).toBeInTheDocument());
        expect(getByText(/Distance/)).toBeInTheDocument();
        // expect(getByTestId(/EarthquakeForm-id/)).toHaveValue("1");
    });


    // test("Correct Error messsages on bad input", async () => {

    //     const { getByTestId, getByText } = render(
    //         <Router  >
    //             <EarthquakeForm />
    //         </Router>
    //     );
    //     await waitFor(() => expect(getByTestId("EarthquakeForm-quarterYYYYQ")).toBeInTheDocument());
    //     const quarterYYYYQField = getByTestId("EarthquakeForm-quarterYYYYQ");
    //     const localDateTimeField = getByTestId("EarthquakeForm-localDateTime");
    //     const submitButton = getByTestId("EarthquakeForm-submit");

    //     fireEvent.change(quarterYYYYQField, { target: { value: 'bad-input' } });
    //     fireEvent.change(localDateTimeField, { target: { value: 'bad-input' } });
    //     fireEvent.click(submitButton);

    //     await waitFor(() => expect(getByText(/QuarterYYYYQ must be in the format YYYYQ/)).toBeInTheDocument());
    //     expect(getByText(/localDateTime must be in ISO format/)).toBeInTheDocument();
    // });

    test("Correct Error messsages on missing input", async () => {

        const { getByTestId, getByText } = render(
            <Router  >
                <EarthquakeForm />
            </Router>
        );
        await waitFor(() => expect(getByTestId("EarthquakeForm-retrieve")).toBeInTheDocument());
        const retrieveButton = getByTestId("EarthquakeForm-retrieve");

        fireEvent.click(retrieveButton);

        await waitFor(() => expect(getByText(/Distance is required./)).toBeInTheDocument());
        expect(getByText(/MinMag is required./)).toBeInTheDocument();

    });

    test("No Error messsages on good input", async () => {

        const mockSubmitAction = jest.fn();


        const { getByTestId, queryByText } = render(
            <Router  >
                <EarthquakeForm submitAction={mockSubmitAction} />
            </Router>
        );
        await waitFor(() => expect(getByTestId("EarthquakeForm-distance")).toBeInTheDocument());

        const distanceField = getByTestId("EarthquakeForm-distance");
        const minMagField = getByTestId("EarthquakeForm-minMag");
        
        const submitButton = getByTestId("EarthquakeForm-retrieve");

        fireEvent.change(distanceField, { target: { value: '1' } });
        fireEvent.change(minMagField, { target: { value: '1' } });
        fireEvent.click(submitButton);

        await waitFor(() => expect(mockSubmitAction).toHaveBeenCalled());

        expect(queryByText(/Distance is required./)).not.toBeInTheDocument();
        expect(queryByText(/MinMag is required./)).not.toBeInTheDocument();

    });


    test("Test that navigate(-1) is called when Cancel is clicked", async () => {

        const { getByTestId } = render(
            <Router  >
                <EarthquakeForm />
            </Router>
        );
        await waitFor(() => expect(getByTestId("EarthquakeForm-cancel")).toBeInTheDocument());
        const cancelButton = getByTestId("EarthquakeForm-cancel");

        fireEvent.click(cancelButton);

        await waitFor(() => expect(mockedNavigate).toHaveBeenCalledWith(-1));

    });

});


