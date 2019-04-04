package com.example.enterc.workmanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {
    JobAdapter jobAdapter;
    List<Job> model, choose;
    ListView listJobs;
    InputCheck inputCheck;
    ImageView dayCalender, addJob;
    TextView dayshow, label_today, done;
    Database database;
    private NotificationCompat.Builder notBuilder;

    private static final int MY_NOTIFICATION_ID = 12345;

    private static final int MY_REQUEST_CODE = 100;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.option,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void addJob() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Bỏ tiêu đề
        dialog.setContentView(R.layout.add_work);
        dialog.show();
        ImageView calender = dialog.findViewById(R.id.add_calender);
        ImageView time_start = dialog.findViewById(R.id.add_time_start);
        ImageView time_end = dialog.findViewById(R.id.add_time_end);
        final EditText show_calender = dialog.findViewById(R.id.show_calender);
        final EditText show_time_start = dialog.findViewById(R.id.show_time_start);
        final EditText show_time_end = dialog.findViewById(R.id.show_time_end);
        final EditText subject = dialog.findViewById(R.id.add_subject);
        final EditText content = dialog.findViewById(R.id.add_content);
        Button save = dialog.findViewById(R.id.save);
        Button cancel = dialog.findViewById(R.id.cancel);
        final Calendar calendar = Calendar.getInstance();
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        show_calender.setText(dayshow.getText().toString());
        // chọn lịch
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = calendar.get(Calendar.DATE);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        show_calender.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        // chọn thời gian bắt đầu
        time_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                final int hour_start = calendar.get(Calendar.HOUR_OF_DAY);
                int minute_start = calendar.get(Calendar.MINUTE);
                TimePickerDialog datePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(0, 0, 0, hourOfDay, minute);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        show_time_start.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, hour_start, minute_start, true);
                datePickerDialog.show();
            }
        });


        // chọn thời gian kết thúc
        time_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                final int hour_start = calendar.get(Calendar.HOUR_OF_DAY);
                int minute_start = calendar.get(Calendar.MINUTE);
                TimePickerDialog datePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(0, 0, 0, hourOfDay, minute);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        show_time_end.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                }, hour_start, minute_start, true);
                datePickerDialog.show();
            }
        });

        // Lưu thông tin công việc
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = show_calender.getText().toString();
                String time_start = show_time_start.getText().toString();
                String time_end = show_time_end.getText().toString();
                String sub = subject.getText().toString();
                String cont = content.getText().toString();
                if (!inputCheck.isValidDate(date)) {
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.dateformat1), Toast.LENGTH_SHORT).show();
                } else if (inputCheck.isValidTime(time_start) == 1) {
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.timeformat1), Toast.LENGTH_SHORT).show();
                } else if (inputCheck.isValidTime(time_start) == 2) {
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.timeformat2), Toast.LENGTH_SHORT).show();
                } else if (inputCheck.isValidTime(time_start) == 3) {
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.timeformat3), Toast.LENGTH_SHORT).show();
                } else if (!inputCheck.isEndthanStart(time_start, time_end)) {
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.startthanend), Toast.LENGTH_SHORT).show();
                } else if (cont.length() == 0) {
                    Toast.makeText(MainActivity.this, getResources().getText(R.string.contentWrong), Toast.LENGTH_SHORT).show();
                } else {
                    //Job job = new Job(date, time_start, time_end, sub, cont, false);
                    String query = "INSERT INTO CongViec VALUES(null,'"+date+"','"+time_start+"','"+time_end+"','"+sub+"','"+cont+"','"+false+"')";
                    database.SQLQuery(query);
                    getData(dayshow.getText().toString());
                    //jobAdapter.add(job);
                    dialog.dismiss();
                }
            }
        });

        // Nhấn cancel nếu không muốn làm gì
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listJobs                = findViewById(R.id.list_jobs);
        dayCalender             = findViewById(R.id.today);
        dayshow                 = findViewById(R.id.day);
        label_today             = findViewById(R.id.label_today);
        done                    = findViewById(R.id.done);
        addJob                  = findViewById(R.id.add_job);
        final Calendar calendar = Calendar.getInstance();
        final int day           = calendar.get(Calendar.DATE);
        final int month         = calendar.get(Calendar.MONTH);
        final int year          = calendar.get(Calendar.YEAR);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dayshow.setText(simpleDateFormat.format(calendar.getTime()));
        database    = new Database(this, "database", null,1);
        database.SQLQuery("CREATE TABLE IF NOT EXISTS CongViec(Id INTEGER PRIMARY KEY AUTOINCREMENT, Date VARCHAR(30),TimeS VARCHAR(10), TimeE VARCHAR(10),Subject VARCHAR(100),Content VARCHAR(1000),Complete VARCHAR(6))");
        model       = new ArrayList<Job>();
        choose = new ArrayList<Job>();
        inputCheck  = new InputCheck();
        jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);
        listJobs.setAdapter(jobAdapter);
        getData(dayshow.getText().toString());
        // notice();

        dayCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year,month,dayOfMonth);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        dayshow.setText(simpleDateFormat.format(calendar.getTime()));
                        getData(dayshow.getText().toString());
                    }
                }, year,month,day);
                datePickerDialog.show();
            }
        });
        // Thêm công việc mới
        addJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addJob();
            }
        });
    }

    // Lấy dữ liệu trong database
    public void getData(String s) {
        Log.d("BBB","ok");
        Cursor cursor = database.SQLSelect("SELECT * FROM CongViec");
        model.clear();
        while (cursor.moveToNext()) {
            if(cursor.getString(6).equals("false"))
            model.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),false));
            if(cursor.getString(6).equals("true"))
            model.add(new Job(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),true));
        }
        JobInDay(s);
        jobAdapter = new JobAdapter(MainActivity.this, R.layout.job_row, choose);
        listJobs.setAdapter(jobAdapter);
    }
    // danh sách công việc theo ngày
    public void JobInDay(String day){
        choose.clear();
        //jobAdapter.clear();
        for(Job i: model){
            if(i.getDate().equals(day)){
                Log.d("XXX",i.toString());
                choose.add(i);
                //jobAdapter.notifyDataSetChanged();
            }
        }
    }
    class JobHolder {
        TextView sub;
        TextView time_detail;
        TextView subj;
        CheckBox checkPass;
    }
    class JobAdapter extends ArrayAdapter<Job>{

        public JobAdapter(Context context, int layout, List<Job> list) {
            super(context, layout,list);
            // super(MainActivity.this, R.layout.job_row, model);

        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
             JobHolder jobHolder;
             View row = convertView;
            if(row==null){
                LayoutInflater inflater  = getLayoutInflater();
                row                      = inflater.inflate(R.layout.job_row, parent, false);
                jobHolder                = new JobHolder();
                jobHolder.sub            = row.findViewById(R.id.sub);
                jobHolder.time_detail    = row.findViewById(R.id.time_detail);
                jobHolder.subj           = row.findViewById(R.id.subj);
                jobHolder.checkPass      = row.findViewById(R.id.checkPass);
                final Job job            = choose.get(position);
                jobHolder.sub.setText(" "+job.getSubject().toString().charAt(0)+" ");
                jobHolder.time_detail.setText(job.getTime_start().toString() + "--" + job.getTime_end().toString());
                jobHolder.subj.setText(job.getSubject());
                jobHolder.checkPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                        Toast.makeText(MainActivity.this,job.isComplete()+"",Toast.LENGTH_SHORT).show();
                        job.setComplete(!job.isComplete());
                    }
                });
                row.setTag(jobHolder);
            }else{
                row.getTag();
            }
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(MainActivity.this,position+"",Toast.LENGTH_SHORT).show();
                    Log.d("TEST",position+"");
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // Bỏ tiêu đề
                    dialog.setContentView(R.layout.work_detail);
                    final EditText subject   = dialog.findViewById(R.id.subject);
                    TextView time            = dialog.findViewById(R.id.time);
                    final EditText detail    = dialog.findViewById(R.id.detail);
                    Button edit        = dialog.findViewById(R.id.button2);
                    Button del         = dialog.findViewById(R.id.button3);
                    Button back        = dialog.findViewById(R.id.button4);
                    final Job job            = choose.get(position);
                    subject.setText(job.getSubject());
                    time.setText(job.getTime_start()+"--"+job.getTime_end()+"----"+job.getDate());
                    detail.setText(job.getContent());
                    dialog.show();
                    // Chỉnh sửa
                    edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            job.setContent(detail.getText().toString());
                            job.setSubject(subject.getText().toString());
                            String update = "UPDATE CongViec SET Subject = '"+subject.getText().toString()+"', Content='"+detail.getText().toString()+"' WHERE Id = '"+job.getId()+"'";
                            database.SQLQuery(update);
                            getData(dayshow.getText().toString());
                            dialog.dismiss();
                        }
                    });
                    // Xóa
                    del.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String delete = "DELETE FROM CongViec WHERE Id  = '"+job.getId()+"'";
                            database.SQLQuery(delete);
                            getData(dayshow.getText().toString());
                            dialog.dismiss();
                        }
                    });
                    // Quay lại
                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            });
            return row;
        }
    }
    // Thông báo cho người dùng sắp có công việc
    public void notice()  {
        this.notBuilder = new NotificationCompat.Builder(this);

        // Thông báo sẽ tự động bị hủy khi người dùng click vào Panel

        this.notBuilder.setAutoCancel(true);
        // --------------------------
        // Chuẩn bị một thông báo
        // --------------------------

        this.notBuilder.setSmallIcon(R.mipmap.calender);
        this.notBuilder.setTicker("This is a ticker");

        // Sét đặt thời điểm sự kiện xẩy ra.
        // Các thông báo trên Panel được sắp xếp bởi thời gian này.
        this.notBuilder.setWhen(System.currentTimeMillis()+ 10* 1000);
        this.notBuilder.setContentTitle("Nhắc nhở");
        this.notBuilder.setContentText("Còn .. để đến công việc tiếp theo, hãy vào để xem");

        // Tạo một Intent
        Intent intent = new Intent(this, MainActivity.class);


        // PendingIntent.getActivity(..) sẽ start mới một Activity và trả về
        // đối tượng PendingIntent.
        // Nó cũng tương đương với gọi Context.startActivity(Intent).
        PendingIntent pendingIntent = PendingIntent.getActivity(this, MY_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        this.notBuilder.setContentIntent(pendingIntent);

        // Lấy ra dịch vụ thông báo (Một dịch vụ có sẵn của hệ thống).
        NotificationManager notificationService  =
                (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);

        // Xây dựng thông báo và gửi nó lên hệ thống.

        Notification notification =  notBuilder.build();
        notificationService.notify(MY_NOTIFICATION_ID, notification);

    }

}
