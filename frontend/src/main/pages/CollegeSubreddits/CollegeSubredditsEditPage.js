import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import { useParams } from "react-router-dom";
import CollegeSubredditForm from "main/components/CollegeSubreddits/CollegeSubredditForm";
import { Navigate } from 'react-router-dom'
import { useBackend, useBackendMutation } from "main/utils/useBackend";
import { toast } from "react-toastify";

export default function CollegeSubredditsEditPage() {
    let { id } = useParams();

    const { data: collegeSubreddit, error: error, status: status } =
        useBackend(
            // Stryker disable next-line all : don't test internal caching of React Query
            [`/api/collegesubreddits?id=${id}`],
            {  // Stryker disable next-line all : GET is the default, so changing this to "" doesn't introduce a bug
                method: "GET",
                url: `/api/collegesubreddits/getbyid`,
                params: {
                    id
                }
            }
        );

    const objectToAxiosPutParams = (collegeSubreddit) => ({
        url: "/api/collegesubreddits/put",
        method: "PUT",
        params: {
            id: collegeSubreddit.id,
        },
        data: {
            name: collegeSubreddit.name,
            location: collegeSubreddit.location,
            subreddit: collegeSubreddit.subreddit
        }
    });

    const onSuccess = (collegeSubreddit) => {
        toast(`CollegeSubreddit Updated - id: ${collegeSubreddit.id} location: ${collegeSubreddit.location}`);
    }

    const mutation = useBackendMutation(
        objectToAxiosPutParams,
        { onSuccess },
        // Stryker disable next-line all : hard to set up test for caching
        [`/api/collegesubreddits?id=${id}`]
    );

    const { isSuccess } = mutation

    const onSubmit = async (data) => {
        mutation.mutate(data);
    }

    if (isSuccess) {
        return <Navigate to="/collegesubreddits/list" />
    }

    return (
        <BasicLayout>
            <div className="pt-2">
                <h1>Edit CollegeSubreddit</h1>
                {collegeSubreddit &&
                    <CollegeSubredditForm initialCollegeSubreddit={collegeSubreddit} submitAction={onSubmit} buttonLabel="Update" />
                }
            </div>
        </BasicLayout>
    )

}
