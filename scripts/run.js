// scripts/run.js
const core = require('@actions/core');
const { execSync } = require('child_process');
const path = require('path');

async function main() {
  try {
    const out = execSync('ls out/src/Day*.class 2>/dev/null | sort -V', {
        encoding: 'utf-8',
      }).trim();

    const classFiles = out ? out.split('\n') : [];
    if (classFiles.length === 0) {
      console.log('No compiled classes named "Day*.class" found in out/src.');
      return;
    }

    // 2) We'll store results in a map from day => array of results
    //    For each day, we keep { className, status, durationMs }
    const resultsByDay = {};

    // 3) Run each class, measure time
    for (const filePath of classFiles) {
      const className = path.basename(filePath, '.class'); // e.g. "Day10Part2"
      const match = className.match(/^Day(\d+)/);
      if (!match) continue;

      const dayNum = match[1];

      // Ensure there's a bucket for this day
      if (!resultsByDay[dayNum]) {
        resultsByDay[dayNum] = [];
      }

      console.log(`Running ${className} (Day ${dayNum})...`);
      const start = Date.now();

      let exitCode = 0;
      let stdout = '';
      try {
        stdout = execSync(`java -cp out/src ${className}`, { encoding: 'utf-8' });
      } catch (error) {
        exitCode = error.status || 1;
        stdout = error.stdout?.toString() || '';
      }
      const end = Date.now();
      const durationMs = end - start;

      console.log(stdout);
      const statusMsg = exitCode === 0 ? 'Success' : 'Failed';
      console.log(`${statusMsg} [${durationMs} ms]\n`);

      // Store the info for this day
      resultsByDay[dayNum].push({
        className,
        status: statusMsg,
        duration: durationMs,
      });
    }

    // 4) Now build the Summary content day by day
    //    Instead of one big table, we create one table *per* day
    core.summary.addHeading('Build & Run Java Files - Summary', 2);

    // Sort the days numerically
    const sortedDays = Object.keys(resultsByDay).sort((a, b) => Number(a) - Number(b));

    for (const day of sortedDays) {
      core.summary.addHeading(`Day ${day}`, 3);

      // Build a small table for this day
      const tableData = [
        [
          { data: 'Class Name', header: true },
          { data: 'Status', header: true },
          { data: 'Duration (ms)', header: true },
        ],
      ];

      for (const row of resultsByDay[day]) {
        tableData.push([
          `\`${row.className}\``,
          row.status,
          row.duration.toString(),
        ]);
      }

      core.summary.addTable(tableData);
    }

    await core.summary.write();

  } catch (err) {
    core.setFailed(`Script error: ${err.message}`);
  }
}

main();
