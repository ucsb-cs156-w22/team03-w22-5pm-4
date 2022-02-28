import { fireEvent, render, waitFor } from "@testing-library/react";
import { earthquakeFixtures } from "fixtures/earthquakeFixtures";
import EarthquakesTable from "main/components/Earthquakes/EarthquakesTable"
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import { currentUserFixtures } from "fixtures/currentUserFixtures";


describe("EarthquakesTable tests", () => {
  const queryClient = new QueryClient();

  test("renders without crashing for empty table with user not logged in", () => {
    const currentUser = null;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <EarthquakesTable earthquakes={[]} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );
  });

  test("renders without crashing for empty table for ordinary user", () => {
    const currentUser = currentUserFixtures.userOnly;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <EarthquakesTable earthquakes={[]} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );
  });

  test("renders without crashing for empty table for admin", () => {
    const currentUser = currentUserFixtures.adminUser;

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <EarthquakesTable earthquakes={[]} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );
  });

  test("Has the expected colum headers and content for adminUser", () => {

    const currentUser = currentUserFixtures.adminUser;

    const { getByText, getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <EarthquakesTable earthquakes={earthquakeFixtures.twoEarthquakes} currentUser={currentUser} />
        </MemoryRouter>
      </QueryClientProvider>

    );

    const expectedHeaders = ["Title", "Mag", "Place", "Time"];
    const expectedFields = ["title", "mag", "place", "time"];
    const testId = "EarthquakesTable";

    expectedHeaders.forEach( (headerText) => {
      const header = getByText(headerText);
      expect(header).toBeInTheDocument();
    } );

    expectedFields.forEach((field) => {
      const header = getByTestId(`${testId}-cell-row-0-col-${field}`);
      expect(header).toBeInTheDocument();
    });

    expect(getByTestId(`${testId}-cell-row-0-col-title`)).toHaveTextContent("Title 1");
    expect(getByTestId(`${testId}-cell-row-1-col-title`)).toHaveTextContent("Title 2");
    expect(getByTestId(`${testId}-cell-row-0-col-mag`)).toHaveTextContent("1");
    expect(getByTestId(`${testId}-cell-row-1-col-mag`)).toHaveTextContent("2");
    expect(getByTestId(`${testId}-cell-row-0-col-place`)).toHaveTextContent("Place 1");
    expect(getByTestId(`${testId}-cell-row-1-col-place`)).toHaveTextContent("Place 2");
    expect(getByTestId(`${testId}-cell-row-0-col-time`)).toHaveTextContent("1");
    expect(getByTestId(`${testId}-cell-row-1-col-time`)).toHaveTextContent("2");

  });

});

