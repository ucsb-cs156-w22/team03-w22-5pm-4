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

    const expectedHeaders = ["id", "Title", "Mag", "Place", "Time"];
    const expectedFields = ["_id","properties.title", "properties.mag", "properties.place", "properties.time"];
    const testId = "EarthquakesTable";

    expectedHeaders.forEach( (headerText) => {
      const header = getByText(headerText);
      expect(header).toBeInTheDocument();
    } );

    expectedFields.forEach((field) => {
      const header = getByTestId(`${testId}-cell-row-0-col-${field}`);
      expect(header).toBeInTheDocument();
    });

    expect(getByTestId(`${testId}-cell-row-0-col-_id`)).toHaveTextContent("1");
    expect(getByTestId(`${testId}-cell-row-1-col-_id`)).toHaveTextContent("2");

    // expect(getByTestId(`${testId}-cell-row-0-col-properties.title`)).toHaveTextContent("Title 1");
    // expect(getByTestId(`${testId}-cell-row-1-col-properties.title`)).toHaveTextContent("Title 2");
    // expect(getByTestId(`${testId}-cell-row-0-col-properties.mag`)).toHaveTextContent("10");
    // expect(getByTestId(`${testId}-cell-row-1-col-properties.mag`)).toHaveTextContent("20");
    // expect(getByTestId(`${testId}-cell-row-0-col-properties.place`)).toHaveTextContent("Place 1");
    // expect(getByTestId(`${testId}-cell-row-1-col-properties.place`)).toHaveTextContent("Place 2");
    // expect(getByTestId(`${testId}-cell-row-0-col-properties.time`)).toHaveTextContent("11");
    // expect(getByTestId(`${testId}-cell-row-1-col-properties.time`)).toHaveTextContent("21");

  });

});

