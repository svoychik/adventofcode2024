const core = require('@actions/core');
const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');


async function main() {
  try {
    // 1) Find all Day*.class files in out/src, sorted by day number
    //    We'll do something like "ls out/src/Day*.class" + sort -V
    let out = '';
    try {
      out = execSync('ls out/src/Day*.class 2>/dev/null | sort -V', {
        encoding: 'utf-8',
      }).trim();
    } catch (e) {
      // Possibly no matching files
    }

    const classFiles = out ? out.split('\n') : [];
    if (classFiles.length === 0) {
      console.log('No compiled classes named Day*.class found.');
      return;
    }

    // 2) Prepare a summary table
    const summaryTable = [
      [
        { data: 'Day', header: true },
        { data: 'Class Name', header: true },
        { data: 'Status', header: true },
      ],
    ];

    let currentDay = null;

    // 3) For each Day*.class, extract the day number, run it, etc.
    for (const filePath of classFiles) {
      // e.g. out/src/Day10.class → Day10
      const base = path.basename(filePath, '.class');
      const className = base; // e.g. Day10

      // Extract the day number from "Day10", "Day10Part2", etc.
      const match = className.match(/^Day(\d+)/);
      if (!match) {
        continue; // skip if it doesn't match "DayNN"
      }
      const dayNum = match[1];

      // If day changed, print a console heading
      if (dayNum !== currentDay) {
        currentDay = dayNum;
        const emoji = '🎄🔹🎄';
        console.log('========================================');
        console.log(` Day ${dayNum} ${emoji}`);
        console.log('========================================\n');
      }

      console.log(`Running ${className}...`);
      let exitCode = 0;
      let stdout = '';
      try {
        // Run the class from out/src
        stdout = execSync(`java -cp out/src ${className}`, { encoding: 'utf-8' });
      } catch (error) {
        exitCode = error.status || 1;
        stdout = error.stdout?.toString() || '';
      }

      console.log(stdout);
      const statusMsg = exitCode === 0 ? '✅ Success' : '❌ Failed';
      console.log(statusMsg, '\n');

      // Append to summary
      summaryTable.push([
        `${dayNum} ${dayEmoji[dayNum] || '🔹'}`,
        `\`${className}\``,
        statusMsg,
      ]);
    }

    // 4) Write the summary
    await core.summary
      .addHeading('Build & Run Java Files - Summary', 2)
      .addTable(summaryTable)
      .write();

  } catch (err) {
    core.setFailed(`Script error: ${err.message}`);
  }
}

main();
