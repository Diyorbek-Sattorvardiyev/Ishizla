package com.example.ishizla.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.ishizla.JobDetailActivity;
import com.example.ishizla.R;
import com.example.ishizla.models.Job;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {
    private Context context;
    private List<Job> jobList;

    public JobAdapter(Context context, List<Job> jobList) {
        this.context = context;
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);

        holder.textTitle.setText(job.getTitle());
        holder.textCompany.setText("Ish beruvchi : "+job.getEmployerName());
        holder.textLocation.setText(job.getLocation());
        holder.textSalary.setText(job.getSalary());
        holder.text_posted_date.setText(job.getCreatedAt());
        // Handle click on job item
        holder.cardView.setOnClickListener(view -> {
            Intent intent = new Intent(context, JobDetailActivity.class);
            intent.putExtra("job_id", job.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public void updateJobList(List<Job> newJobList) {
        this.jobList = newJobList;
        notifyDataSetChanged();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textTitle, textCompany, textLocation, textSalary,text_posted_date;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_job);
            textTitle = itemView.findViewById(R.id.text_job_title);
            textCompany = itemView.findViewById(R.id.text_company_name);
            textLocation = itemView.findViewById(R.id.text_job_location);
            textSalary = itemView.findViewById(R.id.text_job_salary);
            text_posted_date = itemView.findViewById(R.id.text_posted_date);
        }
    }
}
