import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import CollegeSubredditsForm from "main/components/CollegeSubredditsForm/CollegeSubredditForm";
import { Navigate } from 'react-router-dom'
import { useBackendMutation } from "main/utils/useBackend";
import { toast } from "react-toastify";

export default function CollegeSubredditsCreatePage() {
  const objectToAxiosParams = (collegeSubreddit) => ({
    url: "/api/collegesubreddits/post",
    method: "post",
    params: {
      name: collegeSubreddit.name,
      location: collegeSubreddit.location,
      subreddit: collegeSubreddit.subreddit
    }
  });

  const onSuccess = (collegeSubreddit) => {
    toast(`New ucsbDate Created - id: ${collegeSubreddit.id} name: ${collegeSubreddit.name}`);
  }

  const mutation = useBackendMutation(
    objectToAxiosParams,
    { onSuccess },
    // Stryker disable next-line all : hard to set up test for caching
    ["/api/collegesubreddits/all"]
  );

  const { isSuccess } = mutation;

  const onSubmit = async (data) => {
    mutation.mutate(data);
  }

  if (isSuccess) {
    return <Navigate to="/collegesubreddits/list" />
  }


  return (
    <BasicLayout>
      <div className="pt-2">
        <h1>Create New College Subreddit</h1>
        <p>
          <CollegeSubredditsForm submitAction={onSubmit} />
        </p>
      </div>
    </BasicLayout>
  )
}