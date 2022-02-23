import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import UCSBSubjectForm from "main/components/UCSBSubjects/UCSBSubjectForm";
import { Navigate } from 'react-router-dom'
import { useBackendMutation } from "main/utils/useBackend";
import { toast } from "react-toastify";

export default function UCSBSubjectsCreatePage() {

  const objectToAxiosParams = (ucsbSubject) => ({
    url: "/api/UCSBSubjects/post",
    method: "POST",
    params: {
      subjectCode: ucsbSubject.subjectCode,
      subjectTranslation: ucsbSubject.subjectTranslation,
      deptCode: ucsbSubject.deptCode,
      collegeCode: ucsbSubject.collegeCode,
      relatedDeptCode: ucsbSubject.relatedDeptCode,
      inactive: String(ucsbSubject.inactive)
    }
  });

  const onSuccess = (ucsbSubject) => {
    toast(`New UCSBSubject Created - id: ${ucsbSubject.id} name: ${ucsbSubject.subjectCode}`);
  }

  const mutation = useBackendMutation(
    objectToAxiosParams,
     { onSuccess }, 
     // Stryker disable next-line all : hard to set up test for caching
     ["/api/UCSBSubjects/all"]
     );

  const { isSuccess } = mutation

  const onSubmit = async (data) => {
    mutation.mutate(data);
  }

  if (isSuccess) {
    return <Navigate to="/ucsbsubjects/list" />
  }

  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Create New UCSBSubject</h1>

        <UCSBSubjectForm submitAction={onSubmit} />

      </div>
    </BasicLayout>
  )
}