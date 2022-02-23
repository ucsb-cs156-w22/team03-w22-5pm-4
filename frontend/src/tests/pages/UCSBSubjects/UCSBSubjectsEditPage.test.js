import { fireEvent, queryByTestId, render, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import UCSBSubjectsEditPage from "main/pages/UCSBSubjects/UCSBSubjectsEditPage";

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

describe("UCSBSubjectsEditPage tests", () => {

    describe("when the backend doesn't return a todo", () => {

        const axiosMock = new AxiosMockAdapter(axios);

        beforeEach(() => {
            axiosMock.reset();
            axiosMock.resetHistory();
            axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
            axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
            axiosMock.onGet("/api/UCSBSubjects", { params: { id: 17 } }).timeout();
        });

        const queryClient = new QueryClient();
        test("renders header but table is not present", async () => {
            const {getByText, queryByTestId} = render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <UCSBSubjectsEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );
            await waitFor(() => expect(getByText("Edit UCSBSubject")).toBeInTheDocument());
            // expect(queryByTestId("UCSBSubjectForm-quarterYYYYQ")).not.toBeInTheDocument();
        });
    });

    describe("tests where backend is working normally", () => {

        const axiosMock = new AxiosMockAdapter(axios);

        beforeEach(() => {
            axiosMock.reset();
            axiosMock.resetHistory();
            axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
            axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
            axiosMock.onGet("/api/UCSBSubjects", { params: { id: 17 } }).reply(200, {
                id: 17,
                subjectCode: "subjectCode1",
                subjectTranslation: "subjectTranslation1",
                deptCode: "deptCode1",
                collegeCode: "collegeCode1",
                relatedDeptCode: "relatedDeptCode1",
                inactive: true
            });
            axiosMock.onPut('/api/UCSBSubjects').reply(200, {
                id: "17",
                subjectCode: "subjectCode2",
                subjectTranslation: "subjectTranslation2",
                deptCode: "deptCode2",
                collegeCode: "collegeCode2",
                relatedDeptCode: "relatedDeptCode2",
                inactive: true
            });
        });

        const queryClient = new QueryClient();
        test("renders without crashing", () => {
            render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <UCSBSubjectsEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );
        });

        test("Is populated with the data provided", async () => {

            const { getByTestId } = render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <UCSBSubjectsEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );

            await waitFor(() => expect(getByTestId("UCSBSubjectForm-subjectCode")).toBeInTheDocument());

            const idField = getByTestId("UCSBSubjectForm-id");
            const subjectCode = getByTestId("UCSBSubjectForm-subjectCode");
            const subjectTranslation = getByTestId("UCSBSubjectForm-subjectTranslation");
            const deptCode = getByTestId("UCSBSubjectForm-deptCode");
            const collegeCode = getByTestId("UCSBSubjectForm-collegeCode");
            const relatedDeptCode = getByTestId("UCSBSubjectForm-relatedDeptCode");
            const inactive = getByTestId("UCSBSubjectForm-inactive");
            const submitButton = getByTestId("UCSBSubjectForm-submit");

            expect(idField).toHaveValue("17");
            expect(subjectCode).toHaveValue("subjectCode1");
            expect(subjectTranslation).toHaveValue("subjectTranslation1");
            expect(deptCode).toHaveValue("deptCode1");
            expect(collegeCode).toHaveValue("collegeCode1");
            expect(relatedDeptCode).toHaveValue("relatedDeptCode1");
            expect(inactive).toHaveValue("true")
        });

        test("Changes when you click Update", async () => {



            const { getByTestId } = render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <UCSBSubjectsEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );

            await waitFor(() => expect(getByTestId("UCSBSubjectForm-subjectCode")).toBeInTheDocument());

            const idField = getByTestId("UCSBSubjectForm-id");
            const subjectCode = getByTestId("UCSBSubjectForm-subjectCode");
            const subjectTranslation = getByTestId("UCSBSubjectForm-subjectTranslation");
            const deptCode = getByTestId("UCSBSubjectForm-deptCode");
            const collegeCode = getByTestId("UCSBSubjectForm-collegeCode");
            const relatedDeptCode = getByTestId("UCSBSubjectForm-relatedDeptCode");
            const inactive = getByTestId("UCSBSubjectForm-inactive");
            const submitButton = getByTestId("UCSBSubjectForm-submit");

            expect(idField).toHaveValue("17");
            expect(subjectCode).toHaveValue("subjectCode1");
            expect(subjectTranslation).toHaveValue("subjectTranslation1");
            expect(deptCode).toHaveValue("deptCode1");
            expect(collegeCode).toHaveValue("collegeCode1");
            expect(relatedDeptCode).toHaveValue("relatedDeptCode1");
            expect(inactive).toHaveValue("true")

            expect(submitButton).toBeInTheDocument();

            fireEvent.change(subjectCode, { target: { value: 'subjectCode2' } });
            fireEvent.change(subjectTranslation, { target: { value: 'subjectTranslation2' } });
            fireEvent.change(deptCode, { target: { value: 'deptCode2' } });
            fireEvent.change(collegeCode, { target: { value: 'collegeCode2' } });
            fireEvent.change(relatedDeptCode, { target: { value: 'relatedDeptCode2' } });
            fireEvent.change(inactive, { target: { value: true } });

            fireEvent.click(submitButton);

            await waitFor(() => expect(mockToast).toBeCalled);
            expect(mockToast).toBeCalledWith("UCSBSubject Updated - id: 17 subjectCode: subjectCode2");
            expect(mockNavigate).toBeCalledWith({ "to": "/ucsbsubjects/list" });

            expect(axiosMock.history.put.length).toBe(1); // times called
            expect(axiosMock.history.put[0].params).toEqual({ id: 17 });
            expect(axiosMock.history.put[0].data).toBe(JSON.stringify({
                subjectCode: "subjectCode2",
                subjectTranslation: "subjectTranslation2",
                deptCode: "deptCode2",
                collegeCode: "collegeCode2",
                relatedDeptCode: "relatedDeptCode2",
                inactive: "true"
            })); // posted object

        });

       
    });
});


