import { fireEvent, queryByTestId, render, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import CollegeSubredditsEditPage from "main/pages/CollegeSubreddits/CollegeSubredditsEditPage";

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
        useParams: () => ({
            id: 17
        }),
        Navigate: (x) => { mockNavigate(x); return null; }
    };
});

describe("CollegeSubredditsEditPage tests", () => {

    describe("when the backend doesn't return a todo", () => {

        const axiosMock = new AxiosMockAdapter(axios);

        beforeEach(() => {
            axiosMock.reset();
            axiosMock.resetHistory();
            axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
            axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
            axiosMock.onGet("/api/collegesubreddits", { params: { id: 17 } }).timeout();
        });

        const queryClient = new QueryClient();
        test("renders header but table is not present", async () => {
            const { getByText, queryByTestId } = render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <CollegeSubredditsEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );
            await waitFor(() => expect(getByText("Edit CollegeSubreddit")).toBeInTheDocument());
            expect(queryByTestId("CollegeSubredditForm-name")).not.toBeInTheDocument();
        });
    });

    describe("tests where backend is working normally", () => {

        const axiosMock = new AxiosMockAdapter(axios);

        beforeEach(() => {
            axiosMock.reset();
            axiosMock.resetHistory();
            axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
            axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
            axiosMock.onGet("/api/collegesubreddits", { params: { id: 17 } }).reply(200, {
                id: 17,
                name: 'Name 1',
                location: "Location 1",
                subreddit: "Subreddit 1"
            });
            axiosMock.onPut('/api/collegesubreddits').reply(200, {
                id: "17",
                name: 'Name 2',
                location: "Location 2",
                subreddit: "Subreddit 2"
            });
        });

        const queryClient = new QueryClient();
        test("renders without crashing", () => {
            render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <CollegeSubredditsEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );
        });

        test("Is populated with the data provided", async () => {

            const { getByTestId } = render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <CollegeSubredditsEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );

            await waitFor(() => expect(getByTestId("CollegeSubredditForm-name")).toBeInTheDocument());

            const idField = getByTestId("CollegeSubredditForm-id");
            const nameField = getByTestId("CollegeSubredditForm-name");
            const locationField = getByTestId("CollegeSubredditForm-location");
            const subredditField = getByTestId("CollegeSubredditForm-subreddit");
            const submitButton = getByTestId("CollegeSubredditForm-submit");

            expect(idField).toHaveValue("17");
            expect(nameField).toHaveValue("Name 1");
            expect(locationField).toHaveValue("Location 1");
            expect(subredditField).toHaveValue("Subreddit 1");
        });

        test("Changes when you click Update", async () => {



            const { getByTestId } = render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <CollegeSubredditsEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );

            await waitFor(() => expect(getByTestId("CollegeSubredditForm-name")).toBeInTheDocument());

            const idField = getByTestId("CollegeSubredditForm-id");
            const nameField = getByTestId("CollegeSubredditForm-name");
            const locationField = getByTestId("CollegeSubredditForm-location");
            const subredditField = getByTestId("CollegeSubredditForm-subreddit");
            const submitButton = getByTestId("CollegeSubredditForm-submit");

            expect(idField).toHaveValue("17");
            expect(nameField).toHaveValue("Name 1");
            expect(locationField).toHaveValue("Location 1");
            expect(subredditField).toHaveValue("Subreddit 1");

            expect(submitButton).toBeInTheDocument();

            fireEvent.change(nameField, { target: { value: 'Name 2' } })
            fireEvent.change(locationField, { target: { value: 'Location 2' } })
            fireEvent.change(subredditField, { target: { value: "Subreddit 2" } })

            fireEvent.click(submitButton);

            await waitFor(() => expect(mockToast).toBeCalled);
            expect(mockToast).toBeCalledWith("CollegeSubreddit Updated - id: 17 name: Name 2");
            expect(mockNavigate).toBeCalledWith({ "to": "/collegesubreddits/list" });

            expect(axiosMock.history.put.length).toBe(1); // times called
            expect(axiosMock.history.put[0].params).toEqual({ id: 17 });
            expect(axiosMock.history.put[0].data).toBe(JSON.stringify({
                name: 'Name 2',
                location: "Location 2",
                subreddit: "Subreddit 2"
            })); // posted object

        });


    });
});
