import { render } from "@testing-library/react";
import CollegeSubredditsCreatePage from "main/pages/CollegeSubreddits/CollegeSubredditsCreatePage";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";

import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";


const mockToast = jest.fn();
jest.mock('react-toastify', () => {
    const originalModule = jest.requireActual('react-toastify');
    return {
        __esModule: true,
        ...originalModule,
        toast: (x) => mockToast(x)
    };
});

const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => {
    const originalModule = jest.requireActual('react-router-dom');
    return {
        __esModule: true,
        ...originalModule,
        Navigate: (x) => { mockNavigate(x); return null; }
    };
});


describe("CollegeSubredditsCreatePage tests", () => {

    const axiosMock = new AxiosMockAdapter(axios);
    beforeEach(() => {
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    });

    const queryClient = new QueryClient();
    test("renders without crashing", () => {
        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <CollegeSubredditsCreatePage />
                </MemoryRouter>
            </QueryClientProvider>
        );
    });

    test("when you fill in the form and hit submit, it makes a request to the backend", async () => {

        const queryClient = new QueryClient();
        const collegeSubreddit = {
            id: 15,
            name: "UCSB",
            location: "SB",
            subreddit: "ucsbsubreddit"
        };

        axiosMock.onPost("/api/ucollegesubreddits/post").reply(202, collegeSubreddit);

        const { getByTestId } = render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <UCSBDatesCreatePage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        await waitFor(() => {
            expect(getByTestId("CollegeSubredditForm-name")).toBeInTheDocument();
        });

        const nameField = getByTestId("CollegeSubredditForm-name");
        const locationField = getByTestId("CollegeSubredditForm-location");
        const subredditField = getByTestId("CollegeSubredditForm-subreddit");
        const submitButton = getByTestId("CollegeSubredditForm-submit");

        fireEvent.change(nameField, { target: { value: 'UCSB' } });
        fireEvent.change(locationField, { target: { value: 'SB' } });
        fireEvent.change(subredditField, { target: { value: 'ucsbsubreddit' } });

        expect(submitButton).toBeInTheDocument();

        fireEvent.click(submitButton);

        await waitFor(() => expect(axiosMock.history.post.length).toBe(1));

        expect(axiosMock.history.post[0].params).toEqual(
            {
                "name": "UCSB",
                "location": "SB",
                "subreddit": "ucsbreddit"
            });

        expect(mockToast).toBeCalledWith("New collegeSubreddit Created - id: 15 name: UCSB");
        expect(mockNavigate).toBeCalledWith({ "to": "/collegesubreddits/list" });
    });
});
