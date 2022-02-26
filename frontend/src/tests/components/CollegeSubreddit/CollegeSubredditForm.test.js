import { render, waitFor, fireEvent } from "@testing-library/react";
import CollegeSubredditForm from "main/components/CollegeSubreddits/CollegeSubredditForm";
import { collegeSubredditsFixtures } from "fixtures/collegeSubredditsFixtures";
import { BrowserRouter as Router } from "react-router-dom";

const mockedNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockedNavigate
}));


describe("CollegeSubredditForm tests", () => {

    test("renders correctly ", async () => {

        const { getByText } = render(
            <Router  >
                <CollegeSubredditForm />
            </Router>
        );
        await waitFor(() => expect(getByText(/Name/)).toBeInTheDocument());
        await waitFor(() => expect(getByText(/Create/)).toBeInTheDocument());
    });


    test("renders correctly when passing in a CollegeSubreddit ", async () => {

        const { getByText, getByTestId } = render(
            <Router  >
                <CollegeSubredditForm initialCollegeSubreddit={collegeSubredditsFixtures.oneReddit} />
            </Router>
        );
        await waitFor(() => expect(getByTestId(/CollegeSubredditForm-id/)).toBeInTheDocument());
        expect(getByText(/Id/)).toBeInTheDocument();
        expect(getByTestId(/CollegeSubredditForm-id/)).toHaveValue("1");
    });

    test("Correct Error messsages on missing input", async () => {

        const { getByTestId, getByText } = render(
            <Router  >
                <CollegeSubredditForm />
            </Router>
        );
        await waitFor(() => expect(getByTestId("CollegeSubredditForm-submit")).toBeInTheDocument());
        const submitButton = getByTestId("CollegeSubredditForm-submit");

        fireEvent.click(submitButton);

        await waitFor(() => expect(getByText(/Name is required./)).toBeInTheDocument());
        expect(getByText(/Location is required./)).toBeInTheDocument();
        expect(getByText(/Subreddit is required./)).toBeInTheDocument();

    });

    // test("No Error messsages on good input", async () => {

    //     const mockSubmitAction = jest.fn();


    //     const { getByTestId, queryByText } = render(
    //         <Router  >
    //             <CollegeSubredditForm submitAction={mockSubmitAction} />
    //         </Router>
    //     );
    //     await waitFor(() => expect(getByTestId("CollegeSubredditForm-name")).toBeInTheDocument());

    //     const nameField = getByTestId("CollegeSubredditForm-name");
    //     const locationField = getByTestId("CollegeSubredditForm-location");
    //     const subredditField = getByTestId("CollegeSubredditForm-subreddit");
    //     const submitButton = getByTestId("CollegeSubredditForm-submit");

    //     fireEvent.change(nameField, { target: { value: '20221' } });
    //     fireEvent.change(locationField, { target: { value: 'noon on January 2nd' } });
    //     fireEvent.change(subredditField, { target: { value: '2022-01-02T12:00' } });
    //     fireEvent.click(submitButton);

    //     await waitFor(() => expect(mockSubmitAction).toHaveBeenCalled());

    //     //expect(queryByText(/QuarterYYYYQ must be in the format YYYYQ/)).not.toBeInTheDocument();
    //     //expect(queryByText(/localDateTime must be in ISO format/)).not.toBeInTheDocument();

    // });


    test("Test that navigate(-1) is called when Cancel is clicked", async () => {

        const { getByTestId } = render(
            <Router  >
                <CollegeSubredditForm />
            </Router>
        );
        await waitFor(() => expect(getByTestId("CollegeSubredditForm-cancel")).toBeInTheDocument());
        const cancelButton = getByTestId("CollegeSubredditForm-cancel");

        fireEvent.click(cancelButton);

        await waitFor(() => expect(mockedNavigate).toHaveBeenCalledWith(-1));

    });

});


