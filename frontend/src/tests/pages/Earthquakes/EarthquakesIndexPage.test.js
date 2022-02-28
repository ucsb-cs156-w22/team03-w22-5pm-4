import { render, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import EarthquakesIndexPage from "main/pages/Earthquakes/EarthquakesIndexPage";

import { fireEvent } from "@testing-library/react";
import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import { earthquakeFixtures } from "fixtures/earthquakeFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";
import mockConsole from "jest-mock-console";

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

describe("EarthquakesIndexPage tests", () => {

    const axiosMock = new AxiosMockAdapter(axios);
    const testId = "EarthquakesTable";

    const setupUserOnly = () => {
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    };

    const setupAdminUser = () => {
        axiosMock.reset();
        axiosMock.resetHistory();
        axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.adminUser);
        axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
    };

    test("renders without crashing for regular user", () => {
        setupUserOnly();
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/earthquakes/all").reply(200, []);

        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <EarthquakesIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );


    });

    test("renders without crashing for admin user", () => {
        setupAdminUser();
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/earthquakes/all").reply(200, []);

        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <EarthquakesIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );


    });

    test("renders two earthquakes without crashing for regular user", async () => {
        setupUserOnly();
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/earthquakes/all").reply(200, earthquakeFixtures.twoEarthquakes);

        const { getByTestId } = render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <EarthquakesIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        await waitFor(() => { expect(getByTestId(`${testId}-cell-row-0-col-_id`)).toHaveTextContent("1"); });
        expect(getByTestId(`${testId}-cell-row-1-col-_id`)).toHaveTextContent("2");
    });

    test("renders two earthquakes without crashing for admin user", async () => {
        setupAdminUser();
        const queryClient = new QueryClient();
        axiosMock.onGet("/api/earthquakes/all").reply(200, earthquakeFixtures.twoEarthquakes);

        const { getByTestId } = render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <EarthquakesIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        await waitFor(() => { expect(getByTestId(`${testId}-cell-row-0-col-_id`)).toHaveTextContent("1"); });
        expect(getByTestId(`${testId}-cell-row-1-col-_id`)).toHaveTextContent("2");
        expect(getByTestId(`${testId}-cell-row-0-col-properties.title`)).toHaveTextContent("Title 1");
        expect(getByTestId(`${testId}-cell-row-1-col-properties.title`)).toHaveTextContent("Title 2");
        expect(getByTestId(`${testId}-cell-row-0-col-properties.mag`)).toHaveTextContent("10");
        expect(getByTestId(`${testId}-cell-row-1-col-properties.mag`)).toHaveTextContent("20");
        expect(getByTestId(`${testId}-cell-row-0-col-properties.place`)).toHaveTextContent("Place 1");
        expect(getByTestId(`${testId}-cell-row-1-col-properties.place`)).toHaveTextContent("Place 2");
        expect(getByTestId(`${testId}-cell-row-0-col-properties.time`)).toHaveTextContent("11");
        expect(getByTestId(`${testId}-cell-row-1-col-properties.time`)).toHaveTextContent("21");
    });

    test("renders empty table when backend unavailable, user only", async () => {
        setupUserOnly();

        const queryClient = new QueryClient();
        axiosMock.onGet("/api/earthquakes/all").timeout();

        const restoreConsole = mockConsole();

        const { queryByTestId } = render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <EarthquakesIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        await waitFor(() => { expect(axiosMock.history.get.length).toBeGreaterThanOrEqual(1); });

        const errorMessage = console.error.mock.calls[0][0];
        expect(errorMessage).toMatch("Error communicating with backend via GET on /api/earthquakes/all");
        restoreConsole();

        expect(queryByTestId(`${testId}-cell-row-0-col-_id`)).not.toBeInTheDocument();
    });


    test("renders empty table when backend unavailable, user only", async () => {
        setupUserOnly();

        const queryClient = new QueryClient();
        axiosMock.onGet("/api/Earthquakes/all").timeout();

        const restoreConsole = mockConsole();

        const { queryByTestId } = render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <EarthquakesIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        await waitFor(() => { expect(axiosMock.history.get.length).toBeGreaterThanOrEqual(1); });

        const errorMessage = console.error.mock.calls[0][0];
        expect(errorMessage).toMatch("Error communicating with backend via GET on /api/earthquakes/all");
        restoreConsole();

        expect(queryByTestId(`${testId}-cell-row-0-col-id`)).not.toBeInTheDocument();
    });

    test("test what happens when you click purge, admin", async () => {
        setupAdminUser();

        const queryClient = new QueryClient();
        axiosMock.onGet("/api/earthquakes/all").reply(200, earthquakeFixtures.twoEarthquakes);
        axiosMock.onDelete("/api/earthquakes/purge").reply(200, "Successfully deleted all earthquakes!");


        const { getByTestId } = render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <EarthquakesIndexPage />
                </MemoryRouter>
            </QueryClientProvider>
        );

        await waitFor(() => { expect(getByTestId(`${testId}-cell-row-0-col-_id`)).toBeInTheDocument(); });

        expect(getByTestId(`${testId}-cell-row-0-col-_id`)).toHaveTextContent("1"); 


        const purgeButton = getByTestId("purge-button");
        expect(purgeButton).toBeInTheDocument();
       
        fireEvent.click(purgeButton);

        await waitFor(() =>  expect(mockToast).toBeCalledWith("Successfully deleted all earthquakes!"));
       

    });

    

});